package com.bonepeople.android.sdcardcleaner.adapter

import android.animation.ArgbEvaluator
import android.text.format.Formatter
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.ItemFileListBinding
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil
import com.bonepeople.android.widget.util.singleClick

class FileListAdapter(private val dir: FileTreeInfo, private val clickAction: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val textView = TextView(parent.context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            textView.gravity = Gravity.CENTER
            textView.setText(R.string.state_emptyView)
            EmptyHolder(textView)
        } else {
            val views = ItemFileListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataHolder(views)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DataHolder -> {
                holder.views.let { views ->
                    val data = dir.children[position]
                    //设置文件大小比例条
                    val percent = NumberUtil.div(data.size.toDouble(), data.parent?.size?.toDouble() ?: 0.0, 2).toFloat()
                    (views.viewPercent.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentWidth = percent
                    val color = evaluator.evaluate(percent, 0xFFEAD799.toInt(), 0xFFE96E3E.toInt()) as Int
                    views.viewPercent.setBackgroundColor(color)
                    //设置清理标志
                    if (data.rubbish) {
                        views.imageViewRubbish.visibility = ViewGroup.VISIBLE
                    } else {
                        views.imageViewRubbish.visibility = ViewGroup.GONE
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
            }
        }
    }

    override fun getItemCount() = if (dir.children.isEmpty()) 1 else dir.children.size

    override fun getItemViewType(position: Int) = if (dir.children.isEmpty()) 1 else super.getItemViewType(position)

    private class DataHolder(val views: ItemFileListBinding) : RecyclerView.ViewHolder(views.root)
    private class EmptyHolder(textView: TextView) : RecyclerView.ViewHolder(textView)

    companion object {
        private val evaluator = ArgbEvaluator()
    }
}