package com.bonepeople.android.sdcardcleaner.fragment

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.adapter.FileListAdapter
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.FragmentFileListBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import com.bonepeople.android.widget.util.*

class FileListFragment(private val file: FileTreeInfo) : ViewBindingFragment<FragmentFileListBinding>() {
    private val adapter = FileListAdapter(file.children, this)

    override fun initView() {
        views.recyclerView.layoutManager = LinearLayoutManager(activity)
        views.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        views.textViewDelete.singleClick { deleteFiles() }
        views.textViewClean.singleClick { cleanFiles() }
        views.textViewHold.singleClick { saveFiles() }
        views.imageViewClose.singleClick { setMultiSelect(false) }
        views.checkBoxAll.singleClick(0) {
            adapter.updateCheckSet(-1)
            adapter.notifyItemRangeChanged(0, adapter.itemCount, "CheckBox")
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        val title = if (file.path == FileTreeManager.Summary.rootFile.path) {
            getString(R.string.caption_text_mine)
        } else {
            file.name
        }
        ViewTitleBinding.bind(views.titleView).run {
            textViewTitleName.text = title
        }
        views.textViewPath.text = file.path.replace(FileTreeManager.Summary.rootFile.path, getString(R.string.str_path_rootFile))
        if (file.children.isEmpty()) {
            views.textViewEmpty.show()
            views.recyclerView.hide()
        } else {
            views.textViewEmpty.hide()
            views.recyclerView.show()
            views.recyclerView.adapter = adapter
        }
    }

    override fun onBackPressed() {
        if (adapter.multiSelect) {
            setMultiSelect(false)
        } else {
            super.onBackPressed()
        }
    }

    fun clickFile(position: Int) {
        if (adapter.multiSelect) {//处于多选状态
            views.checkBoxAll.isChecked = adapter.updateCheckSet(position)
            adapter.notifyItemChanged(position, "CheckBox")
        } else {//处于浏览状态
            val child = file.children[position]
            if (child.directory) {
                StandardActivity.call(FileListFragment(child)).onResult { adapter.refresh() }
            }
        }
    }

    fun setMultiSelect(selecting: Boolean) {
        adapter.multiSelect = selecting
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "CheckBox")
        if (selecting) {
            views.linearLayoutButtonBar.show()
        } else {
            views.linearLayoutButtonBar.gone()
        }
    }

    private fun deleteFiles() {
        setMultiSelect(false)
        if (adapter.checkedSet.isEmpty()) return
        adapter.refresh()
    }

    private fun cleanFiles() {
        setMultiSelect(false)
        if (adapter.checkedSet.isEmpty()) return
        adapter.refresh()
    }

    private fun saveFiles() {
        setMultiSelect(false)
        if (adapter.checkedSet.isEmpty()) return
        adapter.refresh()
    }
}