package com.bonepeople.android.sdcardcleaner.global.utils

import com.bonepeople.android.widget.util.AppLog

object LogUtil {
    val app by lazy { AppLog.tag("SDAppLog") }
    val test by lazy { AppLog.tag("SDAppLog.Test").apply { showStackInfo = true } }
}