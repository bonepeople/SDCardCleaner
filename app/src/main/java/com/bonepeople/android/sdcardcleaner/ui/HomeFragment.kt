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
import com.bonepeople.android.widget.util.AppToast
import com.bonepeople.android.widget.util.AppView.gone
import com.bonepeople.android.widget.util.AppView.show
import com.bonepeople.android.widget.util.AppView.singleClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    private var state = -1
    private var quit = false

    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_mine)
        views.titleView.views.imageViewTitleAction.run {
            setPadding(DimensionUtil.getPx(16f))
            setImageResource(R.drawable.icon_set)
            singleClick { StandardActivity.open(SettingFragment()) }
            show()
        }
        updateView()
    }

    private fun updateView() {
        if (state != FileTreeManager.currentState) {
            updateState()
        }
        val time = when (state) {
            FileTreeManager.STATE.READY -> ""
            else -> FileTreeManager.getProgressTimeString()
        }
        views.textViewTime.text = time
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

    private fun updateState() {
        state = FileTreeManager.currentState
        when (state) {
            FileTreeManager.STATE.READY -> {
                views.textViewState.setText(R.string.state_ready)
                views.buttonTop.setText(R.string.caption_button_startScan)
                views.buttonTop.singleClick { startScan() }
                views.buttonTop.show()
                views.buttonLeft.gone()
                views.buttonRight.gone()
            }

            FileTreeManager.STATE.SCAN_EXECUTING -> {
                views.textViewState.setText(R.string.state_scan_executing)
                views.buttonTop.setText(R.string.caption_button_stopScan)
                views.buttonTop.singleClick { stopScan() }
                views.buttonTop.show()
                views.buttonLeft.gone()
                views.buttonRight.gone()
            }

            FileTreeManager.STATE.SCAN_STOPPING -> {
                views.textViewState.setText(R.string.state_scan_stopping)
                views.buttonTop.gone()
                views.buttonLeft.gone()
                views.buttonRight.gone()
            }

            FileTreeManager.STATE.SCAN_FINISH -> {
                views.textViewState.setText(R.string.state_scan_finish)
                views.buttonTop.setText(R.string.caption_button_rescan)
                views.buttonLeft.setText(R.string.caption_button_startClean)
                views.buttonRight.setText(R.string.caption_button_viewFiles)
                views.buttonTop.singleClick { startScan() }
                views.buttonLeft.singleClick { startClean() }
                views.buttonRight.singleClick { viewFile() }
                views.buttonTop.show()
                views.buttonLeft.show()
                views.buttonRight.show()
            }

            FileTreeManager.STATE.CLEAN_EXECUTING -> {
                views.textViewState.setText(R.string.state_clean_executing)
                views.buttonTop.setText(R.string.caption_button_stopClean)
                views.buttonTop.singleClick { stopClean() }
                views.buttonTop.show()
                views.buttonLeft.gone()
                views.buttonRight.gone()
            }

            FileTreeManager.STATE.CLEAN_STOPPING -> {
                views.textViewState.setText(R.string.state_clean_stopping)
                views.buttonTop.gone()
                views.buttonLeft.gone()
                views.buttonRight.gone()
            }

            FileTreeManager.STATE.CLEAN_FINISH -> {
                views.textViewState.setText(R.string.state_clean_finish)
                views.buttonTop.setText(R.string.caption_button_rescan)
                views.buttonLeft.setText(R.string.caption_button_startClean)
                views.buttonRight.setText(R.string.caption_button_viewFiles)
                views.buttonTop.singleClick { startScan() }
                views.buttonLeft.singleClick { startClean() }
                views.buttonRight.singleClick { viewFile() }
                views.buttonTop.show()
                views.buttonLeft.show()
                views.buttonRight.show()
            }
        }
    }

    private fun startScan() {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            AppToast.show(getString(R.string.toast_sdcard_error), Toast.LENGTH_LONG)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                FileTreeManager.startScan()
                autoRefresh()
            } else {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:${ApplicationHolder.getPackageName()}"))
                    .launch()
                    .onResult {
                        if (Environment.isExternalStorageManager()) {
                            FileTreeManager.startScan()
                            autoRefresh()
                        } else {
                            AppToast.show(getString(R.string.toast_sdcard_permission))
                        }
                    }
            }
        } else {
            AppPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult { allGranted, _ ->
                    if (allGranted) {
                        FileTreeManager.startScan()
                        autoRefresh()
                    } else {
                        AppToast.show(getString(R.string.toast_sdcard_permission))
                    }
                }
        }
    }

    private fun stopScan() {
        FileTreeManager.stopScan()
    }

    private fun startClean() {
        FileTreeManager.startClean()
        autoRefresh()
    }

    private fun stopClean() {
        FileTreeManager.stopClean()
    }

    private fun viewFile() {
        StandardActivity.call(FileListFragment(FileTreeManager.Summary.rootFile)).onResult { updateView() }
    }

    override fun onBackPressed() {
        if (quit) {
            if (FileTreeManager.currentState == FileTreeManager.STATE.SCAN_EXECUTING) stopScan()
            if (FileTreeManager.currentState == FileTreeManager.STATE.CLEAN_EXECUTING) stopClean()
            FileTreeManager.Summary.rootFile = FileTreeInfo()
            FileTreeManager.currentState = FileTreeManager.STATE.READY
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

    private fun autoRefresh() {
        launch {
            updateView()
            while (FileTreeManager.currentState != FileTreeManager.STATE.SCAN_FINISH && FileTreeManager.currentState != FileTreeManager.STATE.CLEAN_FINISH) {
                delay(500)
                updateView()
            }
            delay(500)
            updateView()
        }
    }
}