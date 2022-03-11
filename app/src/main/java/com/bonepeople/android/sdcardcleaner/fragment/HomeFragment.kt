package com.bonepeople.android.sdcardcleaner.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.databinding.FragmentHomeBinding
import com.bonepeople.android.widget.util.AppPermission
import com.bonepeople.android.widget.util.AppToast

class HomeFragment : ViewBindingFragment<FragmentHomeBinding>() {

    override fun initView() {

    }

    override fun initData(savedInstanceState: Bundle?) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startScan()
        } else {
            AppPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onResult { allGranted, _ ->
                    if (allGranted) {
                        startScan()
                    } else {
                        AppToast.show("需要存储空间的权限才能扫描文件")
                    }
                }
                .launch()
        }
    }


    private fun startScan() {
        views.storageSummary.refresh()
    }
}