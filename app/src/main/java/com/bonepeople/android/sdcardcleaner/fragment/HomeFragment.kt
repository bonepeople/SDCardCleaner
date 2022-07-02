package com.bonepeople.android.sdcardcleaner.fragment

import android.Manifest
import android.os.Bundle
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.widget.util.AppPermission
import com.bonepeople.android.widget.util.AppToast

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {

    override fun initView() {

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


    private fun startScan() {
        views.storageSummary.refresh()
    }
}