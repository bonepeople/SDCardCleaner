package com.bonepeople.android.sdcardcleaner.data

class FileTreeInfo {
    var name = ""//文件名
    var path = ""//文件路径
    var size = 0L//文件大小
    var fileCount = 0//文件夹内文件的总数量，包括子文件夹
    var directory = false//是否是文件夹
    var rubbish = false//是否需要清理
    var sorted = 0//当前排序类型，0-未排序，1-按文件名称排序
    var parent: FileTreeInfo? = null//父目录
    var children = ArrayList<FileTreeInfo>()//子文件列表
}