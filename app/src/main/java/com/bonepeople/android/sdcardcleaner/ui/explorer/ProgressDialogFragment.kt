package com.bonepeople.android.sdcardcleaner.ui.explorer

import androidx.constraintlayout.widget.ConstraintLayout
import com.bonepeople.android.base.viewbinding.ViewBindingDialogFragment
import com.bonepeople.android.sdcardcleaner.databinding.DialogProgressBinding
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppView.singleClick
import com.bonepeople.android.widget.util.AppView.switchShow
import kotlinx.coroutines.launch

class ProgressDialogFragment : ViewBindingDialogFragment<DialogProgressBinding>() {
    private var title: String = ""
    private var percent: Float = 0f
    private var cancelAction: (() -> Unit)? = null
    private var initializedView = false

    override fun initView() {
        dialog?.setCancelable(false)
        updateView()
        views.textViewCancel.switchShow(cancelAction != null)
        views.textViewCancel.singleClick { cancelAction?.invoke() }
        initializedView = true
    }

    private fun updateView() {
        CoroutinesHolder.main.launch {
            views.textViewTitle.text = title
            (views.viewProcess.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = percent
            views.textViewPercent.text = "${(percent * 100).toInt()}%"
        }
    }

    fun setTitle(title: String) = apply {
        this.title = title
        if (initializedView) updateView()
    }

    fun setPercent(percent: Float) = apply {
        this.percent = percent
        if (initializedView) updateView()
    }

    fun onCancel(action: (() -> Unit)?) = apply {
        this.cancelAction = action
        if (initializedView) {
            views.textViewCancel.switchShow(action != null)
        }
    }
}