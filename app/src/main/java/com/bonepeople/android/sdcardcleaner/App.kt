package com.bonepeople.android.sdcardcleaner

import com.bonepeople.android.base.manager.BaseApp

class App : BaseApp() {
    override val appName = "SDCardCleaner"

    companion object {
        const val BUILD_TIME = BuildConfig.buildTime
    }
}