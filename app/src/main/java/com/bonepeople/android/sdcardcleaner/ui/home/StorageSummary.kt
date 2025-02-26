package com.bonepeople.android.sdcardcleaner.ui.home

import android.content.Context
import android.text.format.Formatter
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.data.GlobalSummaryInfo
import com.bonepeople.android.sdcardcleaner.databinding.ViewStorageSummaryBinding

class StorageSummary(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private val views = ViewStorageSummaryBinding.inflate(LayoutInflater.from(context), this, true)
    private val percentConstraintSet = ConstraintSet()

    init {
        initView()
    }

    private fun initView() {
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

    fun updateView(summaryInfo: GlobalSummaryInfo) {
        TransitionManager.beginDelayedTransition(views.blockPercent)
        percentConstraintSet.setGuidelinePercent(R.id.guideLineRubbish, summaryInfo.getRubbishPercent())
        percentConstraintSet.setGuidelinePercent(R.id.guideLineFile, summaryInfo.getFilePercent())
        percentConstraintSet.setGuidelinePercent(R.id.guideLineSystem, summaryInfo.getSystemPercent())
        percentConstraintSet.applyTo(views.blockPercent)

        views.textViewSystem.text = context.getString(R.string.state_fileCount_system, Formatter.formatFileSize(context, summaryInfo.getSystemSize()))
        views.textViewBlank.text = context.getString(R.string.state_fileCount_blank, Formatter.formatFileSize(context, summaryInfo.freeSpace))
        views.textViewFile.text = context.getString(R.string.state_fileCount_file, Formatter.formatFileSize(context, summaryInfo.fileSize), summaryInfo.fileCount)
        views.textViewRubbish.text = context.getString(R.string.state_fileCount_rubbish, Formatter.formatFileSize(context, summaryInfo.rubbishSize), summaryInfo.rubbishCount)
    }
}