package com.bonepeople.android.sdcardcleaner.global

import android.os.Environment
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.utils.CommonUtil
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppTime
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
    }

    object Summary {
        val totalSpace by lazy { Environment.getExternalStorageDirectory().totalSpace }
        val freeSpace by lazy { Environment.getExternalStorageDirectory().freeSpace }
        var rootFile: FileTreeInfo = FileTreeInfo()
        var rubbishCount = 0L
        var rubbishSize = 0L
    }

    private val nameComparator = Comparator<FileTreeInfo> { file1, file2 ->
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

    fun getProgressTimeString(): String {
        if (currentState != STATE.SCAN_FINISH) endTime = System.currentTimeMillis()
        val time = endTime - startTime
        return "(${AppTime.getTimeString(time)}秒)"
    }

    /**
     * 开始扫描文件
     */
    fun startScan() {
        currentState = STATE.SCAN_EXECUTING
        CoroutinesHolder.default.launch {
            startTime = System.currentTimeMillis()
            Summary.rubbishCount = 0
            Summary.rubbishSize = 0
            val file = Environment.getExternalStorageDirectory()
            Summary.rootFile = FileTreeInfo()
            scanFile(null, Summary.rootFile, file)
            currentState = STATE.SCAN_FINISH
            endTime = System.currentTimeMillis()
        }
    }

    /**
     * 停止扫描文件
     */
    fun stopScan() {
        currentState = STATE.SCAN_STOPPING
    }

    private fun scanFile(parentFile: FileTreeInfo?, fileInfo: FileTreeInfo, file: File) {
        //记录基础信息
        fileInfo.parent = parentFile
        fileInfo.name = file.name
        fileInfo.path = file.absolutePath
        fileInfo.directory = file.isDirectory
        fileInfo.size = if (fileInfo.directory) 0 else file.length()
        //垃圾文件的判断
        fileInfo.rubbish = checkRubbish(fileInfo)
        if (fileInfo.rubbish) {
            Summary.rubbishCount++
            Summary.rubbishSize += fileInfo.size
        }
        //将自身添加到上级目录中
        parentFile?.children?.add(fileInfo)
        //更新上级目录的信息
        updateParentFile(fileInfo.parent, 1, fileInfo.size)
        //如果当前是文件夹，对下级所有文件依次遍历
        if (fileInfo.directory) {
            file.listFiles()?.forEach {
                if (currentState == STATE.SCAN_EXECUTING) {
                    scanFile(fileInfo, FileTreeInfo(), it)
                }
            }
            //在当前目录遍历完成后对文件夹内的文件进行排序
            CoroutinesHolder.default.launch {
                fileInfo.children.sortWith(nameComparator)
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
            it.size += size
            it.fileCount += count
            updateParentFile(it.parent, count, size)
        }
    }

    /**
     * 删除指定目录的文件
     * + 包括子目录文件
     */
    suspend fun deleteFile(fileInfo: FileTreeInfo) {
        yield()
        //遍历子目录进行删除
        fileInfo.children.forEach { child ->
            deleteFile(child)
        }
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