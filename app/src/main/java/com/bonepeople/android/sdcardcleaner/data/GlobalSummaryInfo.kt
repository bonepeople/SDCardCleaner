package com.bonepeople.android.sdcardcleaner.data

import com.bonepeople.android.sdcardcleaner.global.utils.NumberUtil

class GlobalSummaryInfo {
    var totalSpace = 0L //总空间
    var freeSpace = 0L //剩余空间
    var fileCount = 0 //文件个数
    var fileSize = 0L //文件大小
    var rubbishCount = 0L //垃圾文件个数
    var rubbishSize = 0L //垃圾文件大小

    fun getSystemSize(): Long {
        return totalSpace - freeSpace - fileSize
    }

    fun getRubbishPercent(): Float {
        return NumberUtil.div(rubbishSize.toDouble(), totalSpace.toDouble(), 3).toFloat()
    }

    fun getFilePercent(): Float {
        return NumberUtil.div(fileSize.toDouble(), totalSpace.toDouble(), 3).toFloat()
    }

    fun getSystemPercent(): Float {
        //此数据仅用于StorageSummary中的展示，并不是单纯的系统空间占比
        return (1 - NumberUtil.div(freeSpace.toDouble(), totalSpace.toDouble(), 3)).toFloat()
    }
}