package com.bonepeople.android.sdcardcleaner

import android.graphics.drawable.ColorDrawable
import com.bonepeople.android.base.manager.BaseApp
import com.bonepeople.android.base.view.TitleView

class App : BaseApp() {
    override val appName = "SDCardCleaner"
    override fun onCreate() {
        super.onCreate()
        TitleView.defaultConfig.apply {
            statusBarBackground = ColorDrawable(getColor(R.color.colorPrimaryDark))
            titleBarBackground = ColorDrawable(getColor(R.color.colorPrimary))
            titleColor = getColor(android.R.color.white)
        }
    }

    companion object {
        const val BUILD_TIME = BuildConfig.buildTime
    }
}