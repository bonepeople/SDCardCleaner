package com.bonepeople.android.sdcardcleaner.data

import com.bonepeople.android.sdcardcleaner.global.utils.CommonUtil

/**
 * 文件信息
 */
class FileTreeInfo {
    var name: String = "" //文件名
    var path: String = "" //文件路径
    var size: Long = 0L //文件大小 //多线程操作时需要注意线程安全
    var fileCount: Int = 0 //文件夹内文件的总数量，包括子文件夹 //多线程操作时需要注意线程安全
    var type: Int = FileType.UNKNOWN //文件类型
    var cleanState: CleanStateInfo = CleanStateInfo() //该文件的自动清理信息
    var sorted: Int = SortType.NONE //当前排序类型
    var parent: FileTreeInfo? = null //父目录
    var children: ArrayList<FileTreeInfo> = ArrayList() //子文件列表 //多线程操作时需要注意线程安全
    var largestFile: FileTreeInfo? = null //当前目录中最大的文件

    fun updateLargestFile() {
        largestFile = null
        children.forEach { child ->
            if (largestFile == null) {
                largestFile = child
            } else {
                if (child.size > largestFile!!.size) {
                    largestFile = child
                }
            }
        }
    }

    //排序类型
    object SortType {
        const val NONE = 0//未排序
        const val NAME_ASC = 1//按文件名称升序排序
        const val SIZE_DESC = 2//按文件大小降序排序
    }

    //文件类型
    object FileType {
        const val UNKNOWN = 0//未知类型
        const val DIRECTORY = 1//文件夹
        const val IMAGE = 2//图片类型
        const val VIDEO = 3//视频类型
    }

    /**
     * 文件名升序比较器
     */
    object NameAscComparator : Comparator<FileTreeInfo> {
        override fun compare(file1: FileTreeInfo, file2: FileTreeInfo): Int {
            return if (file1.type == FileType.DIRECTORY && file2.type == FileType.DIRECTORY) { //两个对比项都是文件夹，按照文件夹名称排序
                CommonUtil.comparePath(file1.name, file2.name)
            } else if (file1.type == FileType.DIRECTORY) { //file1是文件夹，file2是文件，file1排在前面
                -1
            } else if (file2.type == FileType.DIRECTORY) { //file2是文件夹，file1是文件，file2排在前面
                1
            } else { //两个对比项都是文件，按照文件名称排序
                CommonUtil.comparePath(file1.name, file2.name)
            }
        }
    }

    /**
     * 文件大小降序比较器
     */
    object FileSizeDescComparator : Comparator<FileTreeInfo> {
        override fun compare(file1: FileTreeInfo, file2: FileTreeInfo): Int {
            if (file1.size == file2.size) return 0
            return if (file1.size < file2.size) -1 else 1
        }
    }

    /**
     * 自动清理信息
     */
    class CleanStateInfo {
        var enable: Boolean = false
        var count: Int = 0
        var size: Long = 0L
    }
}