package com.bonepeople.android.sdcardcleaner.ui.setting

import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentAboutBinding

class AboutFragment : ViewBindingFragment<FragmentAboutBinding>() {
    override fun initView() {
        views.titleView.title = getString(R.string.caption_text_about)
    }
}