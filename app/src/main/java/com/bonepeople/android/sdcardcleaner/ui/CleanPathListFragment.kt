package com.bonepeople.android.sdcardcleaner.ui

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonepeople.android.base.ViewBindingFragment
import com.bonepeople.android.base.databinding.ViewTitleBinding
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.ui.adapter.CleanPathListAdapter
import com.bonepeople.android.sdcardcleaner.databinding.FragmentCleanPathListBinding
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager

class CleanPathListFragment : ViewBindingFragment<FragmentCleanPathListBinding>() {
    private lateinit var title: ViewTitleBinding
    private val listData = arrayListOf<String>()
    private val adapter = CleanPathListAdapter(listData, this::onItemClick)
    private var mode = WHITE
    override fun initView() {
        title = ViewTitleBinding.bind(views.titleView)
        views.recyclerView.layoutManager = LinearLayoutManager(activity)
        views.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    override fun initData(savedInstanceState: Bundle?) {
        mode = arguments?.getInt("mode", WHITE) ?: WHITE
        val basicPath = Environment.getExternalStorageDirectory().path
        when (mode) {
            WHITE -> {
                title.textViewTitleName.setText(R.string.caption_text_white)
                CleanPathManager.whiteList.forEach {
                    listData.add(it.replace(basicPath, getString(R.string.str_path_rootFile)))
                }
            }
            BLACK -> {
                title.textViewTitleName.setText(R.string.caption_text_black)
                CleanPathManager.blackList.forEach {
                    listData.add(it.replace(basicPath, getString(R.string.str_path_rootFile)))
                }
            }
        }
        views.recyclerView.adapter = adapter
    }

    private fun onItemClick(index: Int) {
        if (FileTreeManager.currentState != FileTreeManager.STATE.SCAN_EXECUTING) {
            AlertDialog.Builder(requireActivity())
                .setMessage(getString(R.string.dialog_list_path_remove, listData[index]))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    if (mode == WHITE) {
                        CleanPathManager.removeWhiteList(index)
                    } else {
                        CleanPathManager.removeBlackList(index)
                    }
                    listData.removeAt(index)
                    adapter.notifyItemRemoved(index)
                    adapter.notifyItemRangeChanged(index, listData.size - index)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
    }

    companion object {
        private const val WHITE = 1
        private const val BLACK = 2
        fun getWhiteList(): CleanPathListFragment {
            return CleanPathListFragment().apply {
                arguments = Bundle().apply {
                    putInt("mode", WHITE)
                }
            }
        }

        fun getBlackList(): CleanPathListFragment {
            return CleanPathListFragment().apply {
                arguments = Bundle().apply {
                    putInt("mode", BLACK)
                }
            }
        }
    }
}