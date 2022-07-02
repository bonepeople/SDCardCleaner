package com.bonepeople.android.sdcardcleaner

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
        const val BUILD_TIME = BuildConfig.buildTime
    }
}