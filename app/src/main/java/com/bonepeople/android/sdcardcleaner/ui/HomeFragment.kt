package com.bonepeople.android.sdcardcleaner.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.view.setPadding
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.util.CoroutineExtension.launchOnIO
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.dimensionutil.DimensionUtil
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.data.GlobalSummaryInfo
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.activity.result.launch
import com.bonepeople.android.widget.util.AppPermission
import com.bonepeople.android.widget.util.AppTime
import com.bonepeople.android.widget.util.AppToast
import com.bonepeople.android.widget.util.AppView.show
import com.bonepeople.android.widget.util.AppView.singleClick
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    private var scanJob: Job? = null
    private var quit = false

    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_mine)
        views.titleView.views.imageViewTitleAction.run {
            setPadding(DimensionUtil.getPx(16f))
            setImageResource(R.drawable.icon_set)
            singleClick { StandardActivity.open(SettingFragment()) }
            show()
        }
        updateSummary()
        views.buttonScan.singleClick { if (scanJob?.isActive == true) stopScan() else checkScanPermission { startScan() } }
        views.buttonClean.singleClick { if (scanJob?.isActive == true) stopClean() else startClean() }
        views.buttonView.singleClick { StandardActivity.call(FileListFragment(FileTreeManager.Summary.rootFile)).onResult { updateSummary() } }
    }

    private fun updateSummary() {
        val summaryInfo = GlobalSummaryInfo().apply {
            totalSpace = FileTreeManager.Summary.totalSpace
            freeSpace = FileTreeManager.Summary.freeSpace
            fileCount = FileTreeManager.Summary.rootFile.fileCount
            fileSize = FileTreeManager.Summary.rootFile.size
            rubbishCount = FileTreeManager.Summary.rootFile.cleanState.count
            rubbishSize = FileTreeManager.Summary.rootFile.cleanState.size
        }
        views.storageSummary.updateView(summaryInfo)
    }

    private fun updateProcessTime(startTime: Long) {
        val time = System.currentTimeMillis() - startTime
        views.textViewTime.text = getString(R.string.state_process_time, AppTime.getTimeString(time))
    }

    private fun checkScanPermission(grantedAction: () -> Unit) {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            AppToast.show(getString(R.string.toast_sdcard_error), Toast.LENGTH_LONG)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                grantedAction()
            } else {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:${ApplicationHolder.getPackageName()}"))
                    .launch()
                    .onResult {
                        if (Environment.isExternalStorageManager()) {
                            grantedAction()
                        } else {
                            AppToast.show(getString(R.string.toast_sdcard_permission))
                        }
                    }
            }
        } else {
            AppPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult { allGranted, _ ->
                    if (allGranted) {
                        grantedAction()
                    } else {
                        AppToast.show(getString(R.string.toast_sdcard_permission))
                    }
                }
        }
    }

    @Suppress("DEPRECATION")
    private fun startScan() {
        views.textViewState.text = getString(R.string.state_scan_executing)
        views.buttonScan.text = getString(R.string.caption_button_stopScan)
        views.buttonClean.isEnabled = false
        views.buttonView.isEnabled = false
        val startTime = System.currentTimeMillis()
        scanJob = launch {
            launchOnIO {
                val file = Environment.getExternalStorageDirectory()
                FileTreeManager.Summary.rootFile = FileTreeInfo()
                //使用Dispatchers.IO调度器开启的协程占用的线程数是有上限的，在所有线程都被使用时，新的协程会等待进行中协程释放线程
                FileTreeManager.scanFile(null, FileTreeManager.Summary.rootFile, file)
                scanJob?.cancel()
            }
            launch {
                while (true) {
                    delay(500)
                    updateProcessTime(startTime)
                    updateSummary()
                }
            }
        }
        scanJob?.invokeOnCompletion {
            launch {
                views.textViewState.text = getString(R.string.state_scan_finish)
                views.buttonScan.text = getString(R.string.caption_button_startScan)
                views.buttonScan.isEnabled = true
                views.buttonClean.isEnabled = true
                views.buttonView.isEnabled = true
                updateProcessTime(startTime)
                updateSummary()
            }
        }
    }

    private fun stopScan() {
        views.textViewState.text = getString(R.string.state_scan_stopping)
        views.buttonScan.isEnabled = false
        scanJob?.cancel()
    }

    private fun startClean() {
        views.textViewState.text = getString(R.string.state_clean_executing)
        views.buttonClean.text = getString(R.string.caption_button_stopClean)
        views.buttonScan.isEnabled = false
        views.buttonView.isEnabled = false
        val startTime = System.currentTimeMillis()
        scanJob = launch {
            launchOnIO {
                FileTreeManager.deleteFile(FileTreeManager.Summary.rootFile, false)
                scanJob?.cancel()
            }
            launch {
                while (true) {
                    delay(500)
                    updateProcessTime(startTime)
                    updateSummary()
                }
            }
        }
        scanJob?.invokeOnCompletion {
            launch {
                views.textViewState.text = getString(R.string.state_clean_finish)
                views.buttonClean.text = getString(R.string.caption_button_startClean)
                views.buttonScan.isEnabled = true
                views.buttonClean.isEnabled = true
                views.buttonView.isEnabled = true
                updateProcessTime(startTime)
                updateSummary()
            }
        }
    }

    private fun stopClean() {
        views.textViewState.text = getString(R.string.state_clean_stopping)
        views.buttonClean.isEnabled = false
        scanJob?.cancel()
    }

    override fun onBackPressed() {
        if (quit) {
            FileTreeManager.Summary.rootFile = FileTreeInfo()
            super.onBackPressed()
        } else {
            AppToast.show(getString(R.string.toast_quitConfirm))
            quit = true
            launch {
                delay(2000)
                quit = false
            }
        }
    }
}