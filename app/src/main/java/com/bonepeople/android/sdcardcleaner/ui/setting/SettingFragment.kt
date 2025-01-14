package com.bonepeople.android.sdcardcleaner.ui.setting

import android.os.Environment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.App
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentSettingBinding
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppLog
import com.bonepeople.android.widget.util.AppRandom
import com.bonepeople.android.widget.util.AppTime
import com.bonepeople.android.widget.util.AppView.singleClick
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.TimeZone

class SettingFragment : ViewBindingFragment<FragmentSettingBinding>() {
    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_set)
        views.textViewWhite.singleClick { StandardActivity.open(CleanPathListFragment.newInstance(CleanPathListFragment.WHITE)) }
        views.textViewBlack.singleClick { StandardActivity.open(CleanPathListFragment.newInstance(CleanPathListFragment.BLACK)) }
        views.textViewAbout.singleClick { StandardActivity.open(AboutFragment()) }
        views.textViewVersion.run {
            val versionName = if (ApplicationHolder.debug) "${ApplicationHolder.getVersionName()} - debug" else ApplicationHolder.getVersionName()
            val buildTime = AppTime.getDateTimeString(App.BUILD_TIME, timeZone = TimeZone.getTimeZone("GMT+8"))
            text = getString(R.string.app_version, versionName, buildTime)
        }
    }

    private fun createFiles() {
        CoroutinesHolder.io.launch {
            loadingDialog.show()
            val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            AppLog.defaultLog.debug(root.absolutePath)
            repeat(10) {
                val dir = File(root, AppRandom.randomString(5))
                dir.mkdirs()
                repeat(100) {
                    val file = File(dir, AppRandom.randomString(5))
                    FileOutputStream(file).use {
                        it.write(AppRandom.randomString(16).toByteArray())
                    }
                }
            }
            loadingDialog.dismiss()
        }
    }
}