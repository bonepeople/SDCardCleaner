package com.bonepeople.android.sdcardcleaner.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.widget.util.AppPermission
import com.bonepeople.android.widget.util.AppToast
import com.bonepeople.android.widget.util.singleClick

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {

    override fun initView() {
        ViewTitleBinding.bind(views.titleView).run {
            imageViewTitleAction.setImageResource(R.drawable.icon_set)
            imageViewTitleAction.visibility = View.VISIBLE
            imageViewTitleAction.singleClick { openSetting() }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        AppPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .onResult { allGranted, _ ->
                if (allGranted) {
                    startScan()
                } else {
                    AppToast.show("需要存储空间的权限才能扫描文件")
                }
            }
    }

    private fun openSetting() {
        StandardActivity.open(activity, SettingFragment())
    }

    private fun startScan() {
        views.storageSummary.refresh()
    }
}