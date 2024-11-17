package com.bonepeople.android.sdcardcleaner.ui.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.util.FlowExtension.observeWithLifecycle
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import com.bonepeople.android.sdcardcleaner.ui.explorer.FileExplorerFragment
import com.bonepeople.android.sdcardcleaner.ui.setting.SettingFragment
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.activity.result.launch
import com.bonepeople.android.widget.util.AppPermission
import com.bonepeople.android.widget.util.AppTime
import com.bonepeople.android.widget.util.AppToast
import com.bonepeople.android.widget.util.AppView.show
import com.bonepeople.android.widget.util.AppView.singleClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private var quit = false

    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_mine)
        views.titleView.views.imageViewTitleAction.run {
            setImageResource(R.drawable.icon_set)
            singleClick { StandardActivity.open(SettingFragment()) }
            show()
        }
        viewModel.currentSummery.observeWithLifecycle(viewLifecycleOwner) { views.storageSummary.updateView(it) }
        viewModel.processTime.observeWithLifecycle(viewLifecycleOwner) {
            val text = if (it == 0L) "" else getString(R.string.state_process_time, AppTime.getTimeString(it))
            views.textViewTime.text = text
        }
        viewModel.pageState.observeWithLifecycle(viewLifecycleOwner) { state: HomeState ->
            when (state) {
                is HomeState.Init -> {
                    views.textViewState.text = getString(R.string.state_ready)
                    views.cardViewScan.isEnabled = false
                    views.cardViewScan.alpha = 0.3f
                    views.textViewScan.text = getString(R.string.caption_button_startScan)
                    views.textViewClean.isEnabled = false
                    views.textViewClean.text = getString(R.string.caption_button_startClean)
                    views.textViewBrowse.isEnabled = false
                }
                is HomeState.Ready -> {
                    views.textViewState.text = getString(R.string.state_ready)
                    views.cardViewScan.isEnabled = true
                    views.cardViewScan.alpha = 1f
                    views.textViewScan.text = getString(R.string.caption_button_startScan)
                    views.textViewClean.isEnabled = false
                    views.textViewClean.text = getString(R.string.caption_button_startClean)
                    views.textViewBrowse.isEnabled = false
                }

                HomeState.ScanExecuting -> {
                    views.textViewState.text = getString(R.string.state_scan_executing)
                    views.cardViewScan.isEnabled = true
                    views.cardViewScan.alpha = 1f
                    views.textViewScan.text = getString(R.string.caption_button_stopScan)
                    views.textViewClean.isEnabled = false
                    views.textViewClean.text = getString(R.string.caption_button_startClean)
                    views.textViewBrowse.isEnabled = false
                }
                HomeState.ScanStopping -> {
                    views.textViewState.text = getString(R.string.state_scan_stopping)
                    views.cardViewScan.isEnabled = false
                    views.cardViewScan.alpha = 0.3f
                    views.textViewScan.text = getString(R.string.caption_button_stopScan)
                    views.textViewClean.isEnabled = false
                    views.textViewClean.text = getString(R.string.caption_button_startClean)
                    views.textViewBrowse.isEnabled = false
                }
                HomeState.ScanFinish -> {
                    views.textViewState.text = getString(R.string.state_scan_finish)
                    views.cardViewScan.isEnabled = true
                    views.cardViewScan.alpha = 1f
                    views.textViewScan.text = getString(R.string.caption_button_startScan)
                    views.textViewClean.isEnabled = true
                    views.textViewClean.text = getString(R.string.caption_button_startClean)
                    views.textViewBrowse.isEnabled = true
                }

                HomeState.CleanExecuting -> {
                    views.textViewState.text = getString(R.string.state_clean_executing)
                    views.cardViewScan.isEnabled = false
                    views.cardViewScan.alpha = 0.3f
                    views.textViewScan.text = getString(R.string.caption_button_startScan)
                    views.textViewClean.isEnabled = true
                    views.textViewClean.text = getString(R.string.caption_button_stopClean)
                    views.textViewBrowse.isEnabled = true
                }
                HomeState.CleanStopping -> {
                    views.textViewState.text = getString(R.string.state_clean_stopping)
                    views.cardViewScan.isEnabled = false
                    views.cardViewScan.alpha = 0.3f
                    views.textViewScan.text = getString(R.string.caption_button_startScan)
                    views.textViewClean.isEnabled = false
                    views.textViewClean.text = getString(R.string.caption_button_stopClean)
                    views.textViewBrowse.isEnabled = true
                }
                HomeState.CleanFinish -> {
                    views.textViewState.text = getString(R.string.state_clean_finish)
                    views.cardViewScan.isEnabled = true
                    views.cardViewScan.alpha = 1f
                    views.textViewScan.text = getString(R.string.caption_button_startScan)
                    views.textViewClean.isEnabled = true
                    views.textViewClean.text = getString(R.string.caption_button_startClean)
                    views.textViewBrowse.isEnabled = true
                }
            }
        }
        views.cardViewScan.singleClick {
            if (viewModel.pageState.value != HomeState.ScanExecuting) {
                checkScanPermission { viewModel.startScan() }
            } else {
                viewModel.stopScan()
            }
        }
        views.textViewClean.singleClick {
            if (viewModel.pageState.value != HomeState.CleanExecuting) {
                viewModel.startClean()
            } else {
                viewModel.stopClean()
            }
        }
        views.textViewBrowse.singleClick {
            StandardActivity.call(FileExplorerFragment.newInstance(FileTreeManager.Summary.rootFile)).onResult {
                viewModel.updateSummary()
            }
        }
    }

    /**
     * 检查扫描权限并在权限被授予时执行指定操作
     */
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

    override fun onBackPressed() {
        if (quit) {
            //退出时清空数据
            FileTreeManager.Summary.rootFile = FileTreeInfo()
            super.onBackPressed()
        } else {
            //2秒内再次点击返回键退出
            AppToast.show(getString(R.string.toast_quitConfirm))
            quit = true
            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                quit = false
            }
        }
    }
}