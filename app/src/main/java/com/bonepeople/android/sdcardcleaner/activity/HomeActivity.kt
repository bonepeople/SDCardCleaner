package com.bonepeople.android.sdcardcleaner.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.bonepeople.android.base.ViewBindingActivity
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.ActivityHomeBinding
import com.bonepeople.android.sdcardcleaner.fragment.HomeFragment

class HomeActivity : ViewBindingActivity<ActivityHomeBinding>() {
    override fun initView() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainerView, HomeFragment())
            commit()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    companion object {
        fun open(activity: Activity?) = activity?.let {
            Intent(it, this::class.java.enclosingClass).run {
                it.startActivity(this)
            }
        }
    }
}