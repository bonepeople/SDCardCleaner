package com.bonepeople.android.sdcardcleaner.data

import com.bonepeople.android.sdcardcleaner.global.utils.NumberUtil

data class GlobalSummaryInfo(
    private val totalSpace: Long = 0L, //总空间
    val freeSpace: Long = 0L, //剩余空间
    val fileCount: Int = 0, //文件个数
    val fileSize: Long = 0L, //文件大小
    val rubbishCount: Int = 0, //垃圾文件个数
    val rubbishSize: Long = 0L, //垃圾文件大小
) {
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