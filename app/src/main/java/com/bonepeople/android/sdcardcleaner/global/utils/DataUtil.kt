package com.bonepeople.android.sdcardcleaner.global.utils

import com.bonepeople.android.widget.util.AppData

object DataUtil {
    val app by lazy { AppData.create("AppData") }

    object AppKey {
        const val ALREADY_TRANSFER_CLEAN_PATH = "ALREADY_TRANSFER_CLEAN_PATH"
        const val WHITE_LIST = "WHITE_LIST"
        const val BLACK_LIST = "BLACK_LIST"
    }
}