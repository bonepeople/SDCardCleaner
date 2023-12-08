package com.bonepeople.android.sdcardcleaner.global

import android.os.Environment
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.widget.CoroutinesHolder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.File
import java.io.FileInputStream

/**
 * 文件树管理器
 */
object FileTreeManager {
    /**
     * 统计信息
     */
    @Suppress("deprecation")
    object Summary {
        val totalSpace by lazy { Environment.getExternalStorageDirectory().totalSpace } //总空间
        val freeSpace by lazy { Environment.getExternalStorageDirectory().freeSpace } //剩余空间
        var rootFile: FileTreeInfo = FileTreeInfo() //根目录
    }

    var scanning = false //是否正在扫描

    /**
     * 扫描文件
     * + 遍历给定文件及其子目录，将信息存储到parentFile中
     * + 会同步更新parentFile中的统计信息
     * @param parentFile 父级目录
     * @param fileInfo 储存file信息的对象
     * @param file 待遍历的文件
     */
    suspend fun scanFile(parentFile: FileTreeInfo?, fileInfo: FileTreeInfo, file: File) {
        //记录基础信息
        fileInfo.parent = parentFile
        fileInfo.name = file.name
        fileInfo.path = file.absolutePath
        if (file.isDirectory) fileInfo.type = FileTreeInfo.FileType.DIRECTORY
        fileInfo.size = if (fileInfo.type == FileTreeInfo.FileType.DIRECTORY) 0 else file.length()
        //更新当前文件的自动清理标志
        fileInfo.cleanState.enable = getCleanState(fileInfo)
        //将自身添加到上级目录中
        parentFile?.children?.let {
            synchronized(it) {
                it.add(fileInfo)
            }
        }
        //更新上级目录的统计信息
        updateParentFile(fileInfo.parent, 1, fileInfo.size)
        //更新上级目录自动清理的统计信息
        if (fileInfo.cleanState.enable) updateParentCleanState(fileInfo.parent, 1, fileInfo.size)
        //如果当前是文件夹，对下级所有文件依次遍历
        if (fileInfo.type == FileTreeInfo.FileType.DIRECTORY) {
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
                fileInfo.children.sortWith(FileTreeInfo.NameAscComparator)
                fileInfo.sorted = 1
            }
        } else {
            //判断文件类型
            CoroutinesHolder.io.launch {
                fileInfo.type = checkFileType(file)
            }
        }
    }

    /**
     * 判断当前文件是否需要被清理
     * + 根据全局的白名单黑名单和父级文件的标记判断当前文件是否需要被清理
     * + 调用此方法前需要确保父级标记为最新状态
     */
    private fun getCleanState(fileInfo: FileTreeInfo): Boolean {
        //在白名单中，不会被清理
        if (CleanPathManager.whiteList.contains(fileInfo.path))
            return false
        //在黑名单中或父级文件夹被标记为清理，本文件需要被清理
        if (CleanPathManager.blackList.contains(fileInfo.path) || fileInfo.parent?.cleanState?.enable == true)
            return true
        //不在任何名单中，默认不需要被清理
        return false
    }

    /**
     * 更新父级文件的统计信息
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
     * 更新父级文件自动清理的统计信息
     * @param count 文件数量变化
     * @param size 文件大小变化
     */
    private fun updateParentCleanState(parentFile: FileTreeInfo?, count: Int, size: Long) {
        parentFile?.let {
            synchronized(it.cleanState) {
                it.cleanState.size += size
                it.cleanState.count += count
            }
            updateParentCleanState(it.parent, count, size)
        }
    }

    /**
     * 检查文件的类型
     */
    private fun checkFileType(file: File): Int {
        val bytes = ByteArray(12)
        runCatching {
            val fileInputStream = FileInputStream(file)
            fileInputStream.read(bytes)
            fileInputStream.close()
        }

        fun ByteArray.startsWith(prefix: ByteArray): Boolean {
            for (i in prefix.indices) {
                if (this[i] != prefix[i]) return false
            }
            return true
        }

        fun ByteArray.containsAtAnyPosition(sequence: ByteArray): Boolean {
            outer@ for (i in this.indices) {
                for (j in sequence.indices) {
                    if (i + j >= this.size || this[i + j] != sequence[j]) continue@outer
                }
                return true
            }
            return false
        }

        return when {
            bytes.startsWith(byteArrayOf(0xFF.toByte(), 0xD8.toByte())) -> FileTreeInfo.FileType.IMAGE // JPEG
            bytes.startsWith(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())) -> FileTreeInfo.FileType.IMAGE // PNG
            bytes.startsWith(byteArrayOf(0x47.toByte(), 0x49.toByte(), 0x46.toByte())) -> FileTreeInfo.FileType.IMAGE // GIF
            bytes.startsWith(byteArrayOf(0x42.toByte(), 0x4D.toByte())) -> FileTreeInfo.FileType.IMAGE // BMP
            bytes.startsWith(byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte())) -> FileTreeInfo.FileType.IMAGE // ICO
            bytes.startsWith(byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0xBA.toByte())) -> FileTreeInfo.FileType.VIDEO // MPEG
            bytes.startsWith(byteArrayOf(0x1A.toByte(), 0x45.toByte(), 0xDF.toByte(), 0xA3.toByte())) -> FileTreeInfo.FileType.VIDEO // MKV
            bytes.startsWith(byteArrayOf(0x52.toByte(), 0x49.toByte(), 0x46.toByte(), 0x46.toByte())) -> FileTreeInfo.FileType.VIDEO // AVI
            bytes.containsAtAnyPosition(byteArrayOf(0x66.toByte(), 0x74.toByte(), 0x79.toByte(), 0x70.toByte())) -> FileTreeInfo.FileType.VIDEO // MP4
            else -> FileTreeInfo.FileType.UNKNOWN
        }
    }

    /**
     * 删除指定目录的文件
     * + 包括子目录文件
     * @param deleteAll 是否删除全部文件，false-仅删除标记为自动清理的文件
     */
    suspend fun deleteFile(fileInfo: FileTreeInfo, deleteAll: Boolean) {
        yield()
        //遍历子目录进行删除
        fileInfo.children.toList().forEach { child ->
            deleteFile(child, deleteAll)
        }
        //删除全部文件或当前文件标记为自动清理的时候执行删除流程
        if (deleteAll || fileInfo.cleanState.enable) {
            //删除当前文件
            val file = File(fileInfo.path)
            if (!file.exists() || file.delete()) {
                //更新上级目录的统计信息
                updateParentFile(fileInfo.parent, -1, -fileInfo.size)
                //更新上级目录自动清理的统计信息
                if (fileInfo.cleanState.enable) updateParentCleanState(fileInfo.parent, -1, -fileInfo.size)
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
    fun updateCleanState(fileInfo: FileTreeInfo) {
        //获取最新清理状态
        val clean = getCleanState(fileInfo)
        //更新上级目录自动清理的统计信息
        if (fileInfo.cleanState.enable && !clean) { //该文件由清理变为保留
            //上级目录的待清理文件数量减1，当前是文件夹待清理文件大小减少0，当前是文件待清理文件大小减少文件大小
            updateParentCleanState(fileInfo.parent, -1, if (fileInfo.type == FileTreeInfo.FileType.DIRECTORY) 0 else -fileInfo.size)
        }
        if (!fileInfo.cleanState.enable && clean) { //该文件由保留变为清理
            //上级目录的待清理文件数量加1，当前是文件夹待清理文件大小增加0，当前是文件待清理文件大小增加文件大小
            updateParentCleanState(fileInfo.parent, 1, if (fileInfo.type == FileTreeInfo.FileType.DIRECTORY) 0 else fileInfo.size)
        }
        //更新清理状态
        fileInfo.cleanState.enable = clean
        //递归更新子项
        fileInfo.children.forEach { child ->
            updateCleanState(child)
        }
    }
}