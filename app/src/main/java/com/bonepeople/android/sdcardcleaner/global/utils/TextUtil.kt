package com.bonepeople.android.sdcardcleaner.global.utils

import com.bonepeople.android.sdcardcleaner.global.text.AppText
import com.bonepeople.android.sdcardcleaner.global.text.AppTextEnUS
import com.bonepeople.android.sdcardcleaner.global.text.AppTextZhCN
import com.bonepeople.android.widget.resource.StringResourceManager
import java.util.Locale

object TextUtil {
    init {
        StringResourceManager.register(AppTextEnUS(), Locale.US)
        StringResourceManager.register(AppTextZhCN(), Locale.SIMPLIFIED_CHINESE)
    }

    val app: AppText
        get() = StringResourceManager.get(AppText.templateClass)
}