package com.bonepeople.android.sdcardcleaner.ui.setting.path

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonepeople.android.base.util.FlowExtension.observeWithLifecycle
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.databinding.FragmentCleanPathListBinding
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import java.io.Serializable

class CleanPathListFragment : ViewBindingFragment<FragmentCleanPathListBinding>() {
    private val viewModel: CleanPathListViewModel by viewModels { ViewModelFactory }
    private val adapter = CleanPathListAdapter().onCLick(::onItemClick)

    override fun initView() {
        views.titleView.title = when (viewModel.mode) {
            Mode.White -> getString(R.string.caption_text_white)
            Mode.Black -> getString(R.string.caption_text_black)
        }
        views.recyclerView.layoutManager = LinearLayoutManager(activity)
        views.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        views.recyclerView.adapter = adapter
        viewModel.listData.observeWithLifecycle(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.status.observeWithLifecycle(viewLifecycleOwner) {
            views.textViewStatus.text = when (it) {
                CleanPathListViewModel.Status.Nothing -> ""
                CleanPathListViewModel.Status.Empty -> getString(R.string.state_emptyView)
            }
        }
    }

    private fun onItemClick(data: String) {
        if (!FileTreeManager.scanning) {
            with(AlertDialog.Builder(requireActivity())) {
                setMessage(getString(R.string.dialog_list_path_remove, data))
                setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.clickItem(data)
                }
                setNegativeButton(android.R.string.cancel, null)
                show()
            }
        }
    }

    sealed class Mode : Serializable {
        object White : Mode()
        object Black : Mode()
    }

    companion object {
        fun newInstance(mode: Mode): CleanPathListFragment {
            return CleanPathListFragment().apply { arguments = bundleOf("mode" to mode) }
        }

        private object ViewModelFactory : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val arguments: Bundle = extras[DEFAULT_ARGS_KEY] ?: Bundle()
                val mode: Mode = arguments.getSerializable("mode") as Mode
                return CleanPathListViewModel(mode) as T
            }
        }
    }
}