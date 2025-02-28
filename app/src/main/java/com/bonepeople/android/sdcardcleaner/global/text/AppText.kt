package com.bonepeople.android.sdcardcleaner.global.text

import com.bonepeople.android.widget.resource.StringTemplate

abstract class AppText : StringTemplate {
    // type
    override val templateClass: Class<out StringTemplate> = AppText.templateClass
    // fields
    abstract val emptyContent: String

    companion object {
        val templateClass = AppText::class.java
    }
}