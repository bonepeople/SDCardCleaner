package com.bonepeople.android.sdcardcleaner.ui.explorer

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.PopupFileSortBinding
import com.bonepeople.android.widget.util.AppView.singleClick

class FileSortPopupWindow(context: Context) : PopupWindow(context) {
    private val views = PopupFileSortBinding.inflate(LayoutInflater.from(context))
    private var selectAction: (Int) -> Unit = { }

    init {
        //设置弹窗的内容
        contentView = views.root
        //改变弹窗背景
        setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
        //设置弹窗外部区域是否响应点击，true-点击外部会关闭弹窗，false-点击外部不会关闭弹窗
        //外部区域的点击会正常被外部控件响应，比如点击外部区域关闭弹窗，但是外部区域是一个按钮，那么按钮的点击事件也会响应
        isOutsideTouchable = true
        //设置弹窗是否屏蔽外部点击事件，true-屏蔽，点击外部区域会关闭弹窗，同时外部事件不响应，false-不屏蔽
        //屏蔽外部点击事件的时候也可响应返回键关闭弹窗
        isFocusable = true

        views.textViewFileName.singleClick {
            selectAction(FileTreeInfo.SortType.NAME_ASC)
            dismiss()
        }
        views.textViewFileSize.singleClick {
            selectAction(FileTreeInfo.SortType.SIZE_DESC)
            dismiss()
        }
    }

    /**
     * 设置选择排序类型的回调
     * @param action 回调函数，参数为排序类型
     */
    fun onSelected(action: (sortType: Int) -> Unit) = apply {
        selectAction = action
    }
}