package com.bonepeople.android.sdcardcleaner.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.activity.result.launch
import com.bonepeople.android.widget.util.AppPermission
import com.bonepeople.android.widget.util.AppToast
import com.bonepeople.android.widget.util.singleClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    private var state = -1

    override fun initView() {
        ViewTitleBinding.bind(views.titleView).run {
            imageViewTitleAction.setImageResource(R.drawable.icon_set)
            imageViewTitleAction.visibility = View.VISIBLE
            imageViewTitleAction.singleClick { StandardActivity.open(SettingFragment()) }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
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
        views.storageSummary.updateView()
    }

    private fun updateState() {
        state = FileTreeManager.currentState
        when (state) {
            FileTreeManager.STATE.READY -> {
                views.textViewState.setText(R.string.state_ready)
                views.buttonTop.setText(R.string.caption_button_startScan)
                views.buttonTop.singleClick { startScan() }
                views.buttonTop.visibility = Button.VISIBLE
                views.buttonLeft.visibility = Button.GONE
                views.buttonRight.visibility = Button.GONE
            }
            FileTreeManager.STATE.SCAN_EXECUTING -> {
                views.textViewState.setText(R.string.state_scan_executing)
                views.buttonTop.setText(R.string.caption_button_stopScan)
                views.buttonTop.singleClick { stopScan() }
                views.buttonTop.visibility = Button.VISIBLE
                views.buttonLeft.visibility = Button.GONE
                views.buttonRight.visibility = Button.GONE
            }
            FileTreeManager.STATE.SCAN_STOPPING -> {
                views.textViewState.setText(R.string.state_scan_stopping)
                views.buttonTop.visibility = Button.GONE
                views.buttonLeft.visibility = Button.GONE
                views.buttonRight.visibility = Button.GONE
            }
            FileTreeManager.STATE.SCAN_FINISH -> {
                views.textViewState.setText(R.string.state_scan_finish)
                views.buttonTop.setText(R.string.caption_button_reScan)
                views.buttonLeft.setText(R.string.caption_button_startClean)
                views.buttonRight.setText(R.string.caption_button_viewFiles)
                views.buttonTop.singleClick { startScan() }
                views.buttonLeft.singleClick { startClean() }
                views.buttonRight.singleClick { viewFile() }
                views.buttonTop.visibility = Button.VISIBLE
                views.buttonLeft.visibility = Button.VISIBLE
                views.buttonRight.visibility = Button.VISIBLE
            }
        }
    }

    private fun startScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                FileTreeManager.startScan()
                autoFresh()
            } else {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:${ApplicationHolder.getPackageName()}"))
                    .launch()
                    .onResult {
                        if (Environment.isExternalStorageManager()) {
                            FileTreeManager.startScan()
                            autoFresh()
                        }
                    }
            }
        } else {
            AppPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult { allGranted, _ ->
                    if (allGranted) {
                        FileTreeManager.startScan()
                        autoFresh()
                    } else {
                        AppToast.show("需要存储空间的权限才能扫描文件")
                    }
                }
        }
    }

    private fun stopScan() {
        FileTreeManager.stopScan()
    }

    private fun startClean() {

    }

    private fun stopClean() {

    }

    private fun viewFile() {

    }

    private fun autoFresh() {
        launch {
            updateView()
            while (FileTreeManager.currentState != FileTreeManager.STATE.SCAN_FINISH) {
                delay(500)
                updateView()
            }
            delay(500)
            updateView()
        }
    }
}