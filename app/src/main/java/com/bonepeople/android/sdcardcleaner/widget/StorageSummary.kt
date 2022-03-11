package com.bonepeople.android.sdcardcleaner.widget

import android.content.Context
import android.os.Environment
import android.text.format.Formatter
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import com.bonepeople.android.sdcardcleaner.Global
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.ViewStorageSummaryBinding
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil
import java.util.*

class StorageSummary(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private val views = ViewStorageSummaryBinding.inflate(LayoutInflater.from(context), this, true)
    private val percentConstraintSet = ConstraintSet()
    private val totalSpace = Environment.getExternalStorageDirectory().totalSpace

    init {
        //将这个控件的Context设置为Locale.ENGLISH，这样在使用Formatter的时候格式化的单位就是MB、GB了，中文系统默认是中文单位
        resources.configuration.setLocale(Locale.ENGLISH)
        views.blockPercent.children.forEachIndexed { index, view ->
            if (view.id == -1) {
                view.id = index + 1
            }
        }
        percentConstraintSet.clone(views.blockPercent)
        refresh()
    }

    fun refresh() {
        val freeSpace = Environment.getExternalStorageDirectory().freeSpace
        val allFileCount = Global.getFileCount_all()
        val allFileSize = Global.getFileSize_all()
        val rubbishCount = Global.getFileCount_rubbish()
        val rubbishSize = Global.getFileSize_rubbish()
        TransitionManager.beginDelayedTransition(views.blockPercent)
        percentConstraintSet.setGuidelinePercent(R.id.guideLine_rubbish, NumberUtil.div(rubbishSize.toDouble(), totalSpace.toDouble(), 3).toFloat())
        percentConstraintSet.setGuidelinePercent(R.id.guideLine_file, NumberUtil.div(allFileSize.toDouble(), totalSpace.toDouble(), 3).toFloat())
        percentConstraintSet.setGuidelinePercent(R.id.guideLine_system, (1 - NumberUtil.div(freeSpace.toDouble(), totalSpace.toDouble(), 3)).toFloat())
        percentConstraintSet.applyTo(views.blockPercent)

        views.textViewCountSystem.text = context.getString(R.string.state_fileCount_system, Formatter.formatFileSize(context, totalSpace - freeSpace - allFileSize))
        views.textViewCountBlank.text = context.getString(R.string.state_fileCount_blank, Formatter.formatFileSize(context, freeSpace))
        views.textViewCountFile.text = context.getString(R.string.state_fileCount_file, Formatter.formatFileSize(context, allFileSize), allFileCount)
        views.textViewCountRubbish.text = context.getString(R.string.state_fileCount_rubbish, Formatter.formatFileSize(context, rubbishSize), rubbishCount)
    }
}