package com.bonepeople.android.sdcardcleaner.ui.adapter

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.widget.util.singleClick

class CleanPathListAdapter(private val list: List<String>, private val clickAction: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val textView = TextView(parent.context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            textView.gravity = Gravity.CENTER
            textView.setText(R.string.state_emptyView)
            EmptyHolder(textView)
        } else {
            val textView = TextView(parent.context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.setPadding(20, 20, 20, 20)
            DataHolder(textView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DataHolder -> {
                holder.textView.text = list[position]
                holder.textView.singleClick { clickAction.invoke(position) }
            }
        }
    }

    override fun getItemCount() = if (list.isEmpty()) 1 else list.size

    override fun getItemViewType(position: Int) = if (list.isEmpty()) 1 else super.getItemViewType(position)

    private class DataHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    private class EmptyHolder(textView: TextView) : RecyclerView.ViewHolder(textView)
}