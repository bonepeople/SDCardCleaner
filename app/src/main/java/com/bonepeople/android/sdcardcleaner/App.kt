package com.bonepeople.android.sdcardcleaner

import android.app.Application
import com.bonepeople.android.base.exception.ExceptionHandler
import com.bonepeople.android.widget.util.AppLog

class App : Application() {
    init {
        AppLog.enable = true
        AppLog.tag = "SDCardCleanerTag"
        ExceptionHandler.setCrashAction { _, _ -> }
    }
}