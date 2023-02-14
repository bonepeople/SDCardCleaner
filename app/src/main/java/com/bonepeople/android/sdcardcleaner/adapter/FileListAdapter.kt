package com.bonepeople.android.sdcardcleaner.adapter

import android.animation.ArgbEvaluator
import android.text.format.Formatter
import androidx.constraintlayout.widget.ConstraintLayout
import com.bonepeople.android.base.ViewBindingRecyclerAdapter
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.ItemFileListBinding
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil
import com.bonepeople.android.widget.util.gone
import com.bonepeople.android.widget.util.show
import com.bonepeople.android.widget.util.singleClick

class FileListAdapter(override val list: List<FileTreeInfo>, private val clickAction: (Int) -> Unit) : ViewBindingRecyclerAdapter<ItemFileListBinding, FileTreeInfo>() {

    override fun onBindView(views: ItemFileListBinding, data: FileTreeInfo, position: Int, payloads: MutableList<Any>) {
        //设置文件大小比例条
        val percent = NumberUtil.div(data.size.toDouble(), data.parent?.size?.toDouble() ?: 0.0, 2).toFloat()
        (views.viewPercent.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = percent
        val color = evaluator.evaluate(percent, 0xFFEAD799.toInt(), 0xFFE96E3E.toInt()) as Int
        views.viewPercent.setBackgroundColor(color)
        //设置清理标志
        if (data.rubbish) {
            views.imageViewRubbish.show()
        } else {
            views.imageViewRubbish.gone()
        }
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
        views.root.singleClick { clickAction(position) }
    }

    companion object {
        private val evaluator = ArgbEvaluator()
    }
}