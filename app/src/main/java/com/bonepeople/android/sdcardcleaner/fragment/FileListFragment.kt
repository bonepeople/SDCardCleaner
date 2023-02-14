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
import com.bonepeople.android.widget.util.hide
import com.bonepeople.android.widget.util.show

class FileListFragment(private val file: FileTreeInfo) : ViewBindingFragment<FragmentFileListBinding>() {
    override fun initView() {
        views.recyclerView.layoutManager = LinearLayoutManager(activity)
        views.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
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
            views.recyclerView.adapter = FileListAdapter(file.children, this::clickFile)
        }
    }

    private fun clickFile(position: Int) {
        val child = file.children[position]
        if (child.directory) {
            StandardActivity.call(FileListFragment(child)).onResult { views.recyclerView.adapter?.notifyDataSetChanged() }
        }
    }
}