package com.bonepeople.android.sdcardcleaner.fragment

import android.os.Bundle
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.sdcardcleaner.databinding.FragmentSettingBinding
import com.bonepeople.android.widget.util.singleClick

class SettingFragment : ViewBindingFragment<FragmentSettingBinding>() {
    override fun initView() {
        views.textViewWhite.singleClick { StandardActivity.open(activity, CleanPathListFragment.getWhiteList()) }
        views.textViewBlack.singleClick { StandardActivity.open(activity, CleanPathListFragment.getBlackList()) }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}