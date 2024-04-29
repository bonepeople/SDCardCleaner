package com.bonepeople.android.sdcardcleaner.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.util.CoroutineExtension.launchOnIO
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
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
    private var coroutineJob: Job? = null
    private var quit = false

    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_mine)
        views.titleView.views.imageViewTitleAction.run {
            setImageResource(R.drawable.icon_set)
            singleClick { StandardActivity.open(SettingFragment()) }
            show()
        }
        updateSummary()
        views.cardViewScan.singleClick { if (coroutineJob?.isActive == true) stopScan() else checkScanPermission { startScan() } }
        views.textViewClean.singleClick { if (coroutineJob?.isActive == true) stopClean() else startClean() }
        views.textViewBrowse.singleClick { StandardActivity.call(FileListFragment(FileTreeManager.Summary.rootFile)).onResult { updateSummary() } }
    }

    /**
     * 更新统计信息
     */
    private fun updateSummary() {
        val summaryInfo = GlobalSummaryInfo(
            totalSpace = FileTreeManager.Summary.totalSpace,
            freeSpace = FileTreeManager.Summary.freeSpace,
            fileCount = FileTreeManager.Summary.rootFile.fileCount,
            fileSize = FileTreeManager.Summary.rootFile.size,
            rubbishCount = FileTreeManager.Summary.rootFile.cleanState.count,
            rubbishSize = FileTreeManager.Summary.rootFile.cleanState.size
        )
        views.storageSummary.updateView(summaryInfo)
    }

    /**
     * 更新处理时间
     */
    private fun updateProcessTime(startTime: Long) {
        val time = System.currentTimeMillis() - startTime
        views.textViewTime.text = getString(R.string.state_process_time, AppTime.getTimeString(time))
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

    /**
     * 开始扫描文件
     */
    @Suppress("deprecation")
    private fun startScan() {
        //更新界面，状态信息、按钮名称、按钮状态
        views.textViewState.text = getString(R.string.state_scan_executing)
        views.textViewScan.text = getString(R.string.caption_button_stopScan)
        views.textViewClean.isEnabled = false
        views.textViewBrowse.isEnabled = false
        val startTime = System.currentTimeMillis() //记录开始时间
        FileTreeManager.scanning = true //设置扫描状态为true
        coroutineJob = viewLifecycleOwner.lifecycleScope.launch {
            launchOnIO { //开启IO协程扫描文件
                val file = Environment.getExternalStorageDirectory()
                FileTreeManager.Summary.rootFile = FileTreeInfo()
                //使用Dispatchers.IO调度器开启的协程占用的线程数是有上限的，在所有线程都被使用时，新的协程会等待进行中协程释放线程
                FileTreeManager.scanFile(null, FileTreeManager.Summary.rootFile, file)
                coroutineJob?.cancel()
            }
            launch { //开启UI协程更新界面，每500毫秒更新一次
                while (true) {
                    delay(500)
                    updateProcessTime(startTime)
                    updateSummary()
                }
            }
        }
        coroutineJob?.invokeOnCompletion { //协程执行完毕后，更新界面
            viewLifecycleOwner.lifecycleScope.launch {
                views.textViewState.text = getString(R.string.state_scan_finish)
                views.textViewScan.text = getString(R.string.caption_button_startScan)
                views.cardViewScan.isEnabled = true
                views.cardViewScan.alpha = 1f
                views.textViewClean.isEnabled = true
                views.textViewBrowse.isEnabled = true
                FileTreeManager.scanning = false
                updateProcessTime(startTime)
                updateSummary()
            }
        }
    }

    /**
     * 停止扫描文件
     */
    private fun stopScan() {
        views.textViewState.text = getString(R.string.state_scan_stopping)
        views.cardViewScan.isEnabled = false
        views.cardViewScan.alpha = 0.3f
        coroutineJob?.cancel()
    }

    /**
     * 开始执行清理逻辑
     */
    private fun startClean() {
        //更新界面，状态信息、按钮名称、按钮状态
        views.textViewState.text = getString(R.string.state_clean_executing)
        views.textViewClean.text = getString(R.string.caption_button_stopClean)
        views.cardViewScan.isEnabled = false
        views.cardViewScan.alpha = 0.3f
        views.textViewBrowse.isEnabled = false
        val startTime = System.currentTimeMillis() //记录开始时间
        coroutineJob = viewLifecycleOwner.lifecycleScope.launch {
            launchOnIO { //开启IO协程清理文件
                FileTreeManager.deleteFile(FileTreeManager.Summary.rootFile, false)
                coroutineJob?.cancel()
            }
            launch { //开启UI协程更新界面，每500毫秒更新一次
                while (true) {
                    delay(500)
                    updateProcessTime(startTime)
                    updateSummary()
                }
            }
        }
        coroutineJob?.invokeOnCompletion { //协程执行完毕后，更新界面
            viewLifecycleOwner.lifecycleScope.launch {
                views.textViewState.text = getString(R.string.state_clean_finish)
                views.textViewClean.text = getString(R.string.caption_button_startClean)
                views.cardViewScan.isEnabled = true
                views.cardViewScan.alpha = 1f
                views.textViewClean.isEnabled = true
                views.textViewBrowse.isEnabled = true
                updateProcessTime(startTime)
                updateSummary()
            }
        }
    }

    /**
     * 停止清理逻辑
     */
    private fun stopClean() {
        views.textViewState.text = getString(R.string.state_clean_stopping)
        views.textViewClean.isEnabled = false
        coroutineJob?.cancel()
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