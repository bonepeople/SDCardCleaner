package com.bonepeople.android.sdcardcleaner.widget

import android.content.Context
import android.text.format.Formatter
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.ViewStorageSummaryBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager as manager
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil

class StorageSummary(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private val views = ViewStorageSummaryBinding.inflate(LayoutInflater.from(context), this, true)
    private val percentConstraintSet = ConstraintSet()

    override fun onFinishInflate() {
        super.onFinishInflate()
        views.blockPercent.children.forEachIndexed { index, view ->
            if (view.id == -1) {
                view.id = index + 1
            }
        }
        percentConstraintSet.clone(views.blockPercent)
        views.textViewSystem.text = context.getString(R.string.state_fileCount_system, Formatter.formatFileSize(context, 0))
        views.textViewBlank.text = context.getString(R.string.state_fileCount_blank, Formatter.formatFileSize(context, 0))
        views.textViewFile.text = context.getString(R.string.state_fileCount_file, Formatter.formatFileSize(context, 0), 0)
        views.textViewRubbish.text = context.getString(R.string.state_fileCount_rubbish, Formatter.formatFileSize(context, 0), 0)
    }

    fun updateView() {
        val allFileCount = manager.rootFile?.fileCount ?: 0
        val allFileSize = manager.rootFile?.size ?: 0
        val rubbishPercent = NumberUtil.div(manager.rubbishSize.toDouble(), manager.totalSpace.toDouble(), 3).toFloat()
        val filePercent = NumberUtil.div(allFileSize.toDouble(), manager.totalSpace.toDouble(), 3).toFloat()
        val systemPercent = (1 - NumberUtil.div(manager.freeSpace.toDouble(), manager.totalSpace.toDouble(), 3)).toFloat()

        TransitionManager.beginDelayedTransition(views.blockPercent)
        percentConstraintSet.setGuidelinePercent(R.id.guideLineRubbish, rubbishPercent)
        percentConstraintSet.setGuidelinePercent(R.id.guideLineFile, filePercent)
        percentConstraintSet.setGuidelinePercent(R.id.guideLineSystem, systemPercent)
        percentConstraintSet.applyTo(views.blockPercent)

        views.textViewSystem.text = context.getString(R.string.state_fileCount_system, Formatter.formatFileSize(context, manager.totalSpace - manager.freeSpace - allFileSize))
        views.textViewBlank.text = context.getString(R.string.state_fileCount_blank, Formatter.formatFileSize(context, manager.freeSpace))
        views.textViewFile.text = context.getString(R.string.state_fileCount_file, Formatter.formatFileSize(context, allFileSize), allFileCount)
        views.textViewRubbish.text = context.getString(R.string.state_fileCount_rubbish, Formatter.formatFileSize(context, manager.rubbishSize), manager.rubbishCount)
    }
}