package com.bonepeople.android.sdcardcleaner.ui.home

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonepeople.android.base.util.CoroutineExtension.launchOnDefault
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.data.GlobalSummaryInfo
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val pageState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState.Init)
    val currentSummery: MutableStateFlow<GlobalSummaryInfo> = MutableStateFlow(GlobalSummaryInfo())
    val processTime: MutableStateFlow<Long> = MutableStateFlow(0)
    private var scanJob: Job? = null
    private var scanStartTime: Long = 0
    private var cleanJob: Job? = null
    private var cleanStartTime: Long = 0

    init {
        viewModelScope.launchOnDefault {
            delay(601)
            updateSummary()
            pageState.value = HomeState.Ready
        }
    }

    fun startScan() {
        scanJob = viewModelScope.launchOnDefault {
            pageState.value = HomeState.ScanExecuting //  更新状态为扫描中
            scanStartTime = System.currentTimeMillis() //  记录开始时间
            FileTreeManager.scanning = true //  设置全局扫描状态为true

            val refreshJob: Job = launch { //  开启协程更新界面，每500毫秒更新一次
                while (true) {
                    delay(500)
                    updateProcessTime(scanStartTime)
                    updateSummary()
                }
            }

            val file = Environment.getExternalStorageDirectory()
            FileTreeManager.Summary.rootFile = FileTreeInfo()
            FileTreeManager.scanFile(null, FileTreeManager.Summary.rootFile, file)
            refreshJob.cancel()
        }
        scanJob?.invokeOnCompletion { //  协程执行完毕后，更新界面
            FileTreeManager.scanning = false
            updateProcessTime(scanStartTime)
            updateSummary()
            pageState.value = HomeState.ScanFinish
        }
    }

    fun stopScan() {
        viewModelScope.launchOnDefault {
            pageState.value = HomeState.ScanStopping
            scanJob?.cancel()
        }
    }

    fun startClean() {
        cleanJob = viewModelScope.launchOnDefault {
            pageState.value = HomeState.CleanExecuting
            cleanStartTime = System.currentTimeMillis()

            val refreshJob: Job = launch { //  开启协程更新界面，每500毫秒更新一次
                while (true) {
                    delay(500)
                    updateProcessTime(cleanStartTime)
                    updateSummary()
                }
            }

            FileTreeManager.deleteFile(FileTreeManager.Summary.rootFile, false)
            refreshJob.cancel()
        }
        cleanJob?.invokeOnCompletion { //  协程执行完毕后，更新界面
            updateProcessTime(scanStartTime)
            updateSummary()
            pageState.value = HomeState.CleanFinish
        }
    }

    fun stopClean() {
        viewModelScope.launchOnDefault {
            pageState.value = HomeState.CleanStopping
            cleanJob?.cancel()
        }
    }

    fun updateSummary() {
        val summaryInfo = GlobalSummaryInfo(
            totalSpace = FileTreeManager.Summary.totalSpace,
            freeSpace = FileTreeManager.Summary.freeSpace,
            fileCount = FileTreeManager.Summary.rootFile.fileCount,
            fileSize = FileTreeManager.Summary.rootFile.size,
            rubbishCount = FileTreeManager.Summary.rootFile.cleanState.count,
            rubbishSize = FileTreeManager.Summary.rootFile.cleanState.size
        )
        currentSummery.value = summaryInfo
    }

    private fun updateProcessTime(startTime: Long) {
        processTime.value = System.currentTimeMillis() - startTime
    }
}