package com.bonepeople.android.sdcardcleaner.data

class FileTreeInfo {
    var name = ""//文件名
    var path = ""//文件路径
    var size = 0L//文件大小 //多线程操作时需要注意线程安全
    var fileCount = 0//文件夹内文件的总数量，包括子文件夹 //多线程操作时需要注意线程安全
    var directory = false//是否是文件夹
    var rubbish = false//是否需要清理
    var sorted = SORT_TYPE_NONE//当前排序类型
    var parent: FileTreeInfo? = null//父目录
    var children = ArrayList<FileTreeInfo>()//子文件列表 //多线程操作时需要注意线程安全
    var largestFile: FileTreeInfo? = null//当前目录中最大的文件

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

    companion object {
        const val SORT_TYPE_NONE = 0//未排序
        const val SORT_TYPE_NAME = 1//按文件名称排序
        const val SORT_TYPE_SIZE = 2//按文件大小排序
    }
}