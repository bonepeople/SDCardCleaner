package com.bonepeople.android.sdcardcleaner.ui.explorer

import android.animation.ArgbEvaluator
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.format.Formatter
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.bonepeople.android.base.viewbinding.ViewBindingRecyclerAdapter
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.ItemFileExplorerBinding
import com.bonepeople.android.sdcardcleaner.global.utils.NumberUtil
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.util.AppView.gone
import com.bonepeople.android.widget.util.AppView.show
import com.bonepeople.android.widget.util.AppView.singleClick
import java.util.TreeSet

class FileExplorerAdapter(override val list: List<FileTreeInfo>, private val fragment: FileExplorerFragment) : ViewBindingRecyclerAdapter<ItemFileExplorerBinding, FileTreeInfo>() {
    var multiSelect = false
        set(value) {
            field = value
            if (value) checkedSet.clear()
        }
    val checkedSet = TreeSet<Int>()

    override fun onBindView(views: ItemFileExplorerBinding, data: FileTreeInfo, position: Int, payloads: List<Any>) {
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
            when {
                data.cleanState.enable -> { //当前文件需要被清理
                    views.imageViewRubbish.show()
                    views.imageViewRubbish.alpha = 1f
                }

                data.cleanState.count > 0 -> { //当前文件不需要被清理，但内部包含需要被清理的文件
                    views.imageViewRubbish.show()
                    views.imageViewRubbish.alpha = 0.2f
                }

                else -> { //当前文件不需要被清理，内部也不包含需要被清理的文件
                    views.imageViewRubbish.gone()
                }
            }
            //设置类型图标
            when (data.type) {
                FileTreeInfo.FileType.DIRECTORY -> views.imageViewType.load(R.drawable.icon_directory)
                FileTreeInfo.FileType.IMAGE -> views.imageViewType.load(data.path)
                FileTreeInfo.FileType.VIDEO -> views.imageViewType.load(data.path)
                FileTreeInfo.FileType.ANDROID -> views.imageViewType.load(data.path)
                else -> views.imageViewType.load(R.drawable.icon_file)
            }
            //设置基本信息
            views.textViewName.text = data.name
            views.textViewDescription.text = when (data.type) {
                FileTreeInfo.FileType.DIRECTORY -> views.root.context.getString(R.string.state_directory_size, Formatter.formatFileSize(views.root.context, data.size), data.fileCount)
                FileTreeInfo.FileType.ANDROID -> {
                    ApplicationHolder.app.packageManager.getPackageArchiveInfo(data.path, PackageManager.GET_ACTIVITIES)?.let { packageInfo: PackageInfo ->
                        val appName = packageInfo.applicationInfo.apply { publicSourceDir = data.path }.loadLabel(ApplicationHolder.app.packageManager)
                        Formatter.formatFileSize(views.root.context, data.size) + " [${appName}-${packageInfo.versionName}]"
                    }
                }

                else -> null
            } ?: Formatter.formatFileSize(views.root.context, data.size)
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