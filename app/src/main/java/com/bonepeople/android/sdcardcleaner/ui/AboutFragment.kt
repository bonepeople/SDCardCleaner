package com.bonepeople.android.sdcardcleaner.ui

import android.os.Bundle
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentAboutBinding

class AboutFragment : ViewBindingFragment<FragmentAboutBinding>() {
    override fun initView() {
        ViewTitleBinding.bind(views.titleView).run {
            textViewTitleName.setText(R.string.caption_text_about)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }
}