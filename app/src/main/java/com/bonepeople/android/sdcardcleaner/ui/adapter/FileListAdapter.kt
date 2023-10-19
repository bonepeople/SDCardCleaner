package com.bonepeople.android.sdcardcleaner.ui.adapter

import android.animation.ArgbEvaluator
import android.text.format.Formatter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bonepeople.android.base.viewbinding.ViewBindingRecyclerAdapter
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.ItemFileListBinding
import com.bonepeople.android.sdcardcleaner.ui.FileListFragment
import com.bonepeople.android.sdcardcleaner.global.utils.NumberUtil
import com.bonepeople.android.widget.util.AppView.gone
import com.bonepeople.android.widget.util.AppView.show
import com.bonepeople.android.widget.util.AppView.singleClick
import com.bonepeople.android.widget.util.AppView.switchShow

class FileListAdapter(override val list: List<FileTreeInfo>, private val fragment: FileListFragment) : ViewBindingRecyclerAdapter<ItemFileListBinding, FileTreeInfo>() {
    var multiSelect = false
        set(value) {
            field = value
            if (value) checkedSet.clear()
        }
    val checkedSet = HashSet<Int>()

    override fun onBindView(views: ItemFileListBinding, data: FileTreeInfo, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            //设置文件大小比例条
            val size = data.size.toDouble()
            val total = data.parent?.largestFile?.size?.toDouble() ?: 0.0
            val percent = if (size > 0 && total > 0) {
                NumberUtil.div(size, total, 2).toFloat()
            } else {
                0f
            }
            (views.viewPercent.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = percent
            val color = evaluator.evaluate(percent, 0xFFEAD799.toInt(), 0xFFE96E3E.toInt()) as Int
            views.viewPercent.setBackgroundColor(color)
            //设置清理标志
            views.imageViewRubbish.switchShow(data.cleanState.enable)
            //设置类型图标
            if (data.directory) {
                views.imageViewType.setImageResource(R.drawable.icon_directory)
            } else {
                views.imageViewType.setImageResource(R.drawable.icon_file)
            }
            //设置基本信息
            views.textViewName.text = data.name
            views.textViewDescription.text = if (data.directory) {
                views.root.context.getString(R.string.state_directory_size, Formatter.formatFileSize(views.root.context, data.size), data.fileCount)
            } else {
                Formatter.formatFileSize(views.root.context, data.size)
            }
            views.root.singleClick(50) { fragment.clickFile(position) }
            views.root.setOnLongClickListener {
                if (multiSelect) {
                    fragment.clickFile(position)
                } else {
                    fragment.setMultiSelect(true)
                    fragment.clickFile(position)
                }
                return@setOnLongClickListener true
            }
        }
        //设置复选框状态
        if (multiSelect) {
            views.checkBox.show()
            views.checkBox.isChecked = checkedSet.contains(position)
        } else {
            views.checkBox.gone()
        }
    }

    fun updateCheckSet(position: Int): Boolean {
        if (position == -1) {//全选标记
            if (checkedSet.size == list.size) {//已全选
                checkedSet.clear()
            } else {//未全选
                checkedSet.addAll(list.indices)
            }
        } else {//普通位置序号
            if (checkedSet.contains(position)) {
                checkedSet.remove(position)
            } else {
                checkedSet.add(position)
            }
        }
        return checkedSet.size == list.size
    }

    fun refresh() {
        notifyItemRangeChanged(0, list.size)
    }

    companion object {
        private val evaluator = ArgbEvaluator()
    }
}