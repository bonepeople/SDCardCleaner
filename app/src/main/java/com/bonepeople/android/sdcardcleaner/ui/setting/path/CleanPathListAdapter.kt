package com.bonepeople.android.sdcardcleaner.ui.setting.path

import com.bonepeople.android.base.viewbinding.ViewBindingRefreshAdapter
import com.bonepeople.android.sdcardcleaner.data.StringDiffCallback
import com.bonepeople.android.sdcardcleaner.databinding.ItemPathListBinding
import com.bonepeople.android.widget.util.AppView.singleClick

class CleanPathListAdapter : ViewBindingRefreshAdapter<ItemPathListBinding, String>(StringDiffCallback) {
    private var clickAction: (data: String) -> Unit = {}

    override fun onBindView(views: ItemPathListBinding, data: String, position: Int, payloads: List<Any>) {
        views.textView.text = data
        views.textView.singleClick { clickAction(data) }
    }

    fun onCLick(action: (data: String) -> Unit) = apply {
        clickAction = action
    }
}