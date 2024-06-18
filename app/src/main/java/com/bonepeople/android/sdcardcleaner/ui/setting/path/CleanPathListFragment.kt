package com.bonepeople.android.sdcardcleaner.ui.setting.path

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentCleanPathListBinding
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import com.bonepeople.android.widget.util.AppToast

class CleanPathListFragment : ViewBindingFragment<FragmentCleanPathListBinding>() {
    private val viewModel: CleanPathListViewModel by viewModels { ViewModelFactory }
    private val listData = arrayListOf<String>()
    private val adapter = CleanPathListAdapter(listData, this::onItemClick)
    private var mode = Mode.WHITE
    override fun initView() {
        views.recyclerView.layoutManager = LinearLayoutManager(activity)
        views.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    @Suppress("deprecation")
    override fun initData(savedInstanceState: Bundle?) {
        mode = arguments?.getInt("mode", Mode.WHITE) ?: Mode.WHITE
        val basicPath = Environment.getExternalStorageDirectory().path
        when (mode) {
            Mode.WHITE -> {
                views.titleView.title = getString(R.string.caption_text_white)
                CleanPathManager.whiteList.forEach {
                    listData.add(it.replace(basicPath, getString(R.string.str_path_rootFile)))
                }
            }

            Mode.BLACK -> {
                views.titleView.title = getString(R.string.caption_text_black)
                CleanPathManager.blackList.forEach {
                    listData.add(it.replace(basicPath, getString(R.string.str_path_rootFile)))
                }
            }

            else -> {
                AppToast.show("mode error")
            }
        }
        views.recyclerView.adapter = adapter
    }

    private fun onItemClick(index: Int) {
        if (!FileTreeManager.scanning) {
            AlertDialog.Builder(requireActivity())
                .setMessage(getString(R.string.dialog_list_path_remove, listData[index]))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    if (mode == Mode.WHITE) {
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

    object Mode {
        const val UNKNOWN = 0
        const val WHITE = 1
        const val BLACK = 2
    }

    companion object {
        fun newInstance(mode: Int): CleanPathListFragment {
            return CleanPathListFragment().apply { arguments = bundleOf("mode" to mode) }
        }

        private object ViewModelFactory : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val arguments: Bundle = extras[DEFAULT_ARGS_KEY] ?: Bundle()
                val mode: Int = arguments.getInt("mode", Mode.UNKNOWN)
                return CleanPathListViewModel(mode) as T
            }
        }
    }
}