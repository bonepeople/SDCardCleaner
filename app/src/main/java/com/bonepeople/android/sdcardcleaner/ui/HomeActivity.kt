package com.bonepeople.android.sdcardcleaner.ui

import android.os.Bundle
import com.bonepeople.android.base.viewbinding.ViewBindingActivity
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.ActivityHomeBinding

class HomeActivity : ViewBindingActivity<ActivityHomeBinding>() {
    override fun initView() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, HomeFragment())
            commit()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}