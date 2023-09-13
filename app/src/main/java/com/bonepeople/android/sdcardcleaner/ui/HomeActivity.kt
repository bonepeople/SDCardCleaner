package com.bonepeople.android.sdcardcleaner.ui

import android.os.Bundle
import com.bonepeople.android.base.viewbinding.ViewBindingActivity
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.ActivityHomeBinding

class HomeActivity : ViewBindingActivity<ActivityHomeBinding>() {
    override fun initView() {
        var fragment = supportFragmentManager.findFragmentByTag("HomeFragment")
        if (fragment == null) {
            fragment = HomeFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, fragment, "HomeFragment").commit()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}