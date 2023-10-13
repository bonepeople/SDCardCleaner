package com.bonepeople.android.sdcardcleaner.global

import android.os.Environment
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.global.utils.CommonUtil
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File

@Suppress("DEPRECATION")
object FileTreeManager {
    object STATE {
        const val READY = 0
        const val SCAN_EXECUTING = 1
        const val SCAN_STOPPING = 2
        const val SCAN_FINISH = 3
        const val CLEAN_EXECUTING = 4
        const val CLEAN_STOPPING = 5
        const val CLEAN_FINISH = 6
    }

    object Summary {
        val totalSpace by lazy { Environment.getExternalStorageDirectory().totalSpace }
        val freeSpace by lazy { Environment.getExternalStorageDirectory().freeSpace }
        var rootFile: FileTreeInfo = FileTreeInfo()
        var rubbishCount = 0L //多线程操作时需要注意线程安全
        var rubbishSize = 0L //多线程操作时需要注意线程安全
    }

    val nameComparator = Comparator<FileTreeInfo> { file1, file2 ->
        if (file1.directory && file2.directory) {
            CommonUtil.comparePath(file1.name, file2.name)
        } else if (file1.directory) {
            -1
        } else if (file2.directory) {
            1
        } else {
            CommonUtil.comparePath(file1.name, file2.name)
        }
    }
    var currentState = STATE.READY
    private var startTime = 0L
    private var endTime = 0L
    private var scanJob: Job? = null
    private var cleanJob: Job? = null

    fun getProgressTimeString(): String {
        if (currentState != STATE.SCAN_FINISH && currentState != STATE.CLEAN_FINISH) endTime = System.currentTimeMillis()
        val time = endTime - startTime
        return "(${AppTime.getTimeString(time)}秒)"
    }

    /**
     * 开始扫描文件
     */
    fun startScan() {
        currentState = STATE.SCAN_EXECUTING
        startTime = System.currentTimeMillis()
        scanJob = CoroutinesHolder.io.launch {
            Summary.rubbishCount = 0
            Summary.rubbishSize = 0
            val file = Environment.getExternalStorageDirectory()
            Summary.rootFile = FileTreeInfo()
            //使用Dispatchers.IO调度器开启的协程占用的线程数是有上限的，在所有线程都被使用时，新的协程会等待进行中协程释放线程
            scanFile(null, Summary.rootFile, file)
        }
        scanJob?.invokeOnCompletion {
            currentState = STATE.SCAN_FINISH
            endTime = System.currentTimeMillis()
        }
    }

    /**
     * 停止扫描文件
     */
    fun stopScan() {
        currentState = STATE.SCAN_STOPPING
        scanJob?.cancel()
    }

    /**
     * 开始执行清理逻辑
     */
    fun startClean() {
        currentState = STATE.CLEAN_EXECUTING
        startTime = System.currentTimeMillis()
        cleanJob = CoroutinesHolder.io.launch {
            deleteFile(Summary.rootFile, false)
        }
        cleanJob?.invokeOnCompletion {
            currentState = STATE.CLEAN_FINISH
            endTime = System.currentTimeMillis()
        }
    }

    /**
     * 停止清理逻辑
     */
    fun stopClean() {
        currentState = STATE.CLEAN_STOPPING
        cleanJob?.cancel()
    }

    /**
     * 扫描文件
     * + 遍历给定文件及其子目录，将信息存储到parentFile中
     * + 会同步更新parentFile中的统计信息
     * @param parentFile 父级目录
     * @param fileInfo 储存file信息的对象
     * @param file 待遍历的文件
     */
    private suspend fun scanFile(parentFile: FileTreeInfo?, fileInfo: FileTreeInfo, file: File) {
        //记录基础信息
        fileInfo.parent = parentFile
        fileInfo.name = file.name
        fileInfo.path = file.absolutePath
        fileInfo.directory = file.isDirectory
        fileInfo.size = if (fileInfo.directory) 0 else file.length()
        //垃圾文件的判断
        fileInfo.rubbish = checkRubbish(fileInfo)
        if (fileInfo.rubbish) {
            synchronized(Summary) {
                Summary.rubbishCount++
                Summary.rubbishSize += fileInfo.size
            }
        }
        //将自身添加到上级目录中
        parentFile?.children?.let {
            synchronized(it) {
                it.add(fileInfo)
            }
        }
        //更新上级目录的信息
        updateParentFile(fileInfo.parent, 1, fileInfo.size)
        //如果当前是文件夹，对下级所有文件依次遍历
        if (fileInfo.directory) {
            //在协程取消的时候忽略CancellationException异常，使后面的逻辑正常执行
            runCatching {
                //使用coroutineScope创建一个协程作用域，在子协程全部完成后才会继续执行
                //协程取消的检查点位于coroutineScope的末端，已取消的协程会在coroutineScope结束后抛出异常
                coroutineScope {
                    //已取消的协程也可以正常进入coroutineScope，为防止无意义的遍历，在协程被取消时提前返回
                    if (!isActive) return@coroutineScope
                    //遍历当前目录下的所有文件
                    file.listFiles()?.forEach {
                        //为防止无意义的遍历，在协程被取消时提前返回
                        //不提前返回也不会产生异常，已处于取消状态的coroutineScope不会再创建新的协程
                        if (!isActive) return@coroutineScope
                        launch {
                            scanFile(fileInfo, FileTreeInfo(), it)
                        }
                    }
                }
            }
            //在当前目录遍历完成后进行一些额外工作
            CoroutinesHolder.default.launch {
                //确定文件夹内最大的文件
                fileInfo.updateLargestFile()
                //对文件夹内的文件进行排序
                fileInfo.children.sortWith(nameComparator)
                fileInfo.sorted = 1
            }
        }
    }

    /**
     * 垃圾文件的判断
     * + 根据全局的白名单黑名单和父级文件的标记判断当前文件是否需要被清理
     * + 调用此方法前需要确保父级标记为最新状态
     */
    private fun checkRubbish(fileInfo: FileTreeInfo): Boolean {
        //在白名单中，不会被清理
        if (CleanPathManager.whiteList.contains(fileInfo.path))
            return false
        //在黑名单中或父级文件夹被标记为清理，本文件需要被清理
        if (CleanPathManager.blackList.contains(fileInfo.path) || fileInfo.parent?.rubbish == true)
            return true
        //不在任何名单中，默认不需要被清理
        return false
    }

    /**
     * 更新父级文件信息
     * @param count 文件数量变化
     * @param size 文件大小变化
     */
    private fun updateParentFile(parentFile: FileTreeInfo?, count: Int, size: Long) {
        parentFile?.let {
            synchronized(it) {
                it.size += size
                it.fileCount += count
            }
            updateParentFile(it.parent, count, size)
        }
    }

    /**
     * 删除指定目录的文件
     * + 包括子目录文件
     * @param deleteAll 是否删除全部文件，false-仅删除标记为垃圾的文件
     */
    suspend fun deleteFile(fileInfo: FileTreeInfo, deleteAll: Boolean) {
        yield()
        //遍历子目录进行删除
        fileInfo.children.toList().forEach { child ->
            deleteFile(child, deleteAll)
        }
        //删除全部文件或当前文件标记为垃圾的时候执行删除流程
        if (deleteAll || fileInfo.rubbish) {
            //删除当前文件
            val file = File(fileInfo.path)
            if (!file.exists() || file.delete()) {
                //更新垃圾文件统计
                if (fileInfo.rubbish) {
                    Summary.rubbishCount--
                    Summary.rubbishSize -= fileInfo.size
                }
                //更新父级文件信息
                updateParentFile(fileInfo.parent, -1, -fileInfo.size)
                //移除父级引用
                fileInfo.parent?.children?.remove(fileInfo)
                //成功删除后，递归更新上级目录中最大的文件
                var child: FileTreeInfo = fileInfo
                var parent: FileTreeInfo? = fileInfo.parent
                while (parent != null) {
                    if (parent.largestFile == child) {
                        parent.updateLargestFile()
                    }
                    child = parent
                    parent = parent.parent
                }
            }
        }
    }

    /**
     * 更新文件及子文件的待清理状态
     */
    fun updateRubbish(fileInfo: FileTreeInfo) {
        //获取最新清理状态
        val rubbish = checkRubbish(fileInfo)
        //更新垃圾文件的统计结果
        if (fileInfo.rubbish && !rubbish) {//该文件由清理变为保留
            Summary.rubbishCount--
            if (!fileInfo.directory) {//只统计文件的大小
                Summary.rubbishSize -= fileInfo.size
            }
        }
        if (!fileInfo.rubbish && rubbish) {//该文件由保留变为清理
            Summary.rubbishCount++
            if (!fileInfo.directory) {//只统计文件的大小
                Summary.rubbishSize += fileInfo.size
            }
        }
        //更新清理状态
        fileInfo.rubbish = rubbish
        //递归更新子项
        fileInfo.children.forEach { child ->
            updateRubbish(child)
        }
    }
}