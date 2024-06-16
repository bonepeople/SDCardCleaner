package com.bonepeople.android.sdcardcleaner.ui.setting

import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.App
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentSettingBinding
import com.bonepeople.android.sdcardcleaner.ui.setting.test.TestFragment
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.util.AppTime
import com.bonepeople.android.widget.util.AppView.singleClick
import com.bonepeople.android.widget.util.AppView.switchShow
import java.util.TimeZone

class SettingFragment : ViewBindingFragment<FragmentSettingBinding>() {
    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_set)
        views.textViewWhite.singleClick { StandardActivity.open(CleanPathListFragment.newInstance(CleanPathListFragment.WHITE)) }
        views.textViewBlack.singleClick { StandardActivity.open(CleanPathListFragment.newInstance(CleanPathListFragment.BLACK)) }
        views.textViewAbout.singleClick { StandardActivity.open(AboutFragment()) }
        views.textViewTest.switchShow(ApplicationHolder.debug) { it.singleClick { StandardActivity.open(TestFragment()) } }
        views.textViewVersion.run {
            val versionName = if (ApplicationHolder.debug) "${ApplicationHolder.getVersionName()} - debug" else ApplicationHolder.getVersionName()
            val buildTime = AppTime.getDateTimeString(App.BUILD_TIME, timeZone = TimeZone.getTimeZone("GMT+8"))
            text = getString(R.string.app_version, versionName, buildTime)
        }
    }
}