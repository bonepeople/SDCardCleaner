package com.bonepeople.android.sdcardcleaner.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
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
            imageViewTitleAction.singleClick { StandardActivity.open(activity, SettingFragment()) }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        updateView()
    }

    private fun updateView() {
        if (state != FileTreeManager.state) {
            updateState()
        }
        views.storageSummary.updateView()
    }

    private fun updateState() {
        state = FileTreeManager.state
        when (state) {
            FileTreeManager.STATE_READY -> {
                views.textViewState.setText(R.string.state_ready)
                views.buttonTop.setText(R.string.caption_button_startScan)
                views.buttonTop.singleClick { startScan() }
                views.buttonTop.visibility = Button.VISIBLE
                views.buttonLeft.visibility = Button.GONE
                views.buttonRight.visibility = Button.GONE
            }
            FileTreeManager.STATE_SCAN_EXECUTING -> {
                views.textViewState.setText(R.string.state_scan_executing)
                views.buttonTop.setText(R.string.caption_button_stopScan)
                views.buttonTop.singleClick { stopScan() }
                views.buttonTop.visibility = Button.VISIBLE
                views.buttonLeft.visibility = Button.GONE
                views.buttonRight.visibility = Button.GONE
            }
            FileTreeManager.STATE_SCAN_STOPPING -> {
                views.textViewState.setText(R.string.state_scan_stopping)
                views.buttonTop.visibility = Button.GONE
                views.buttonLeft.visibility = Button.GONE
                views.buttonRight.visibility = Button.GONE
            }
            FileTreeManager.STATE_SCAN_FINISH -> {
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
            do {
                delay(150)
                updateView()
            } while (FileTreeManager.state != FileTreeManager.STATE_SCAN_FINISH)
            updateView()
        }
    }
}