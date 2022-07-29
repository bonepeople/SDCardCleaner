package com.bonepeople.android.sdcardcleaner.fragment

import android.os.Bundle
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.App
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentSettingBinding
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.util.AppTime
import com.bonepeople.android.widget.util.singleClick

class SettingFragment : ViewBindingFragment<FragmentSettingBinding>() {
    override fun initView() {
        ViewTitleBinding.bind(views.titleView).run {
            textViewTitleName.setText(R.string.caption_text_set)
        }
        views.textViewWhite.singleClick { StandardActivity.open(CleanPathListFragment.getWhiteList()) }
        views.textViewBlack.singleClick { StandardActivity.open(CleanPathListFragment.getBlackList()) }
        views.textViewAbout.singleClick { StandardActivity.open(AboutFragment()) }
        views.textViewVersion.run {
            val versionName = if (ApplicationHolder.debug) "${ApplicationHolder.getVersionName()} - debug" else ApplicationHolder.getVersionName()
            val buildTime = AppTime.getDateTimeString(App.BUILD_TIME)
            text = getString(R.string.app_version, versionName, buildTime)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}