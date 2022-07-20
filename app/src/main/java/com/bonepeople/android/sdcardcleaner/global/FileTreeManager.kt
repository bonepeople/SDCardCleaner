package com.bonepeople.android.sdcardcleaner.global

import android.os.Environment
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.widget.CoroutinesHolder
import kotlinx.coroutines.launch
import java.io.File

object FileTreeManager {
    const val STATE_READY = 0
    const val STATE_SCAN_EXECUTING = 1
    const val STATE_SCAN_STOPPING = 2
    const val STATE_SCAN_FINISH = 3
    val totalSpace by lazy { Environment.getExternalStorageDirectory().totalSpace }
    val freeSpace by lazy { Environment.getExternalStorageDirectory().freeSpace }
    var rootFile: FileTreeInfo? = null
    var rubbishCount = 0L
    var rubbishSize = 0L
    var state = STATE_READY

    fun startScan() {
        state = STATE_SCAN_EXECUTING
        CoroutinesHolder.default.launch {
            rubbishCount = 0
            rubbishSize = 0
            val file = Environment.getExternalStorageDirectory()
            rootFile = FileTreeInfo()
            scanFile(null, rootFile!!, file)
            state = STATE_SCAN_FINISH
        }
    }

    fun stopScan() {
        state = STATE_SCAN_STOPPING
    }

    private fun scanFile(parentFile: FileTreeInfo?, fileInfo: FileTreeInfo, file: File) {
        fileInfo.parent = parentFile
        fileInfo.name = file.name
        fileInfo.path = file.absolutePath
        fileInfo.directory = file.isDirectory
        fileInfo.size = if (fileInfo.directory) 0 else file.length()
        //垃圾文件的判断
        when (true) {
            CleanPathManager.whiteList.contains(fileInfo.path) -> {
                fileInfo.rubbish = false
            }
            CleanPathManager.blackList.contains(fileInfo.path), fileInfo.parent?.rubbish -> {
                fileInfo.rubbish = true
                rubbishCount++
                rubbishSize += fileInfo.size
            }
            else -> {
                fileInfo.rubbish = false
            }
        }
        parentFile?.children?.add(fileInfo)
        updateParentFile(fileInfo.parent, fileInfo)

        if (fileInfo.directory) {
            file.listFiles()?.forEach {
                if (state == STATE_SCAN_EXECUTING) {
                    scanFile(fileInfo, FileTreeInfo(), it)
                }
            }
        }
    }

    private fun updateParentFile(parentFile: FileTreeInfo?, fileInfo: FileTreeInfo) {
        parentFile?.let {
            it.size += fileInfo.size
            it.fileCount += 1
            updateParentFile(it.parent, fileInfo)
        }
    }
}