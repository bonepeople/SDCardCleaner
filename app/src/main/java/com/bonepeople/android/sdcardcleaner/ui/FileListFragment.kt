package com.bonepeople.android.sdcardcleaner.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonepeople.android.base.activity.StandardActivity
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.ui.adapter.FileListAdapter
import com.bonepeople.android.sdcardcleaner.data.FileTreeInfo
import com.bonepeople.android.sdcardcleaner.databinding.FragmentFileListBinding
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager
import com.bonepeople.android.sdcardcleaner.global.FileTreeManager
import com.bonepeople.android.sdcardcleaner.ui.view.SortSelectorPopupWindow
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppToast
import com.bonepeople.android.widget.util.AppView.singleClick
import com.bonepeople.android.widget.util.AppView.switchShow
import com.bonepeople.android.widget.util.AppView.switchVisible
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FileListFragment(private val file: FileTreeInfo) : ViewBindingFragment<FragmentFileListBinding>() {
    private val adapter = FileListAdapter(file.children, this)

    override fun initView() {
        views.titleView.onActionClick {
            SortSelectorPopupWindow(requireContext()).onSelected { sortType ->
                if (file.sorted == sortType) return@onSelected //选择想用的排序方式，不再重复排序
                when (sortType) {
                    FileTreeInfo.SortType.NAME_ASC -> {
                        launch {
                            file.children.sortWith(FileTreeInfo.NameAscComparator)
                            adapter.refresh()
                        }
                    }

                    FileTreeInfo.SortType.SIZE_DESC -> {
                        launch {
                            file.children.sortWith(FileTreeInfo.FileSizeDescComparator)
                            adapter.refresh()
                        }
                    }

                    else -> return@onSelected
                }
                file.sorted = sortType
            }.showAsDropDown(views.titleView, 0, 0, Gravity.END)
        }
        views.recyclerView.layoutManager = LinearLayoutManager(activity)
        views.recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        views.textViewDelete.singleClick { deleteFiles() }
        views.textViewClean.singleClick { updateFileRubbish(true) }
        views.textViewHold.singleClick { updateFileRubbish(false) }
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
        views.titleView.title = title
        views.textViewPath.text = file.path.replace(FileTreeManager.Summary.rootFile.path, getString(R.string.str_path_rootFile))
        views.textViewEmpty.switchVisible(file.children.isEmpty())
        views.recyclerView.switchVisible(file.children.isNotEmpty()) { recyclerView: RecyclerView ->
            recyclerView.adapter = adapter
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
            if (child.type == FileTreeInfo.FileType.DIRECTORY) { //文件夹
                StandardActivity.call(FileListFragment(child)).onResult { adapter.refresh() }
            } else { //文件
                kotlin.runCatching {
                    val uri = Uri.parse(child.path)
                    val intent = Intent(Intent.ACTION_VIEW)
                    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(child.path))
                    intent.setDataAndType(uri, mimeType)
                    startActivity(intent)
                }.getOrElse {
                    AppToast.show("cannot open")
                }
            }
        }
    }

    fun setMultiSelect(selecting: Boolean) {
        adapter.multiSelect = selecting
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "CheckBox")
        views.linearLayoutButtonBar.switchShow(selecting)
    }

    /**
     * 删除所选文件
     */
    private fun deleteFiles() {
        //取消多选状态
        setMultiSelect(false)
        if (adapter.checkedSet.isEmpty()) return
        //弹出删除确认框
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.dialog_list_file_delete_title))
        builder.setMessage(getString(R.string.dialog_list_file_delete_message))
        builder.setCancelable(false)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            //删除流程
            var job: Job? = null
            val notifyItems = ArrayList<Int>()
            //弹出删除进度对话框
            val progressDialog = ProgressDialog(requireActivity())
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.setCancelable(false)
            progressDialog.setMessage(getString(R.string.dialog_list_file_deleting))
            progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel)) { _, _ ->
                job?.cancel()
            }
            progressDialog.max = adapter.checkedSet.size
            progressDialog.show()
            //删除文件的协程
            job = CoroutinesHolder.io.launch {
                adapter.checkedSet.forEach { position ->
                    FileTreeManager.deleteFile(file.children[position - notifyItems.size], true)
                    notifyItems.add(position - notifyItems.size)
                    progressDialog.incrementProgressBy(1)
                }
            }
            //文件删除流程结束后进行刷新
            job.invokeOnCompletion {
                CoroutinesHolder.main.launch {
                    notifyItems.forEach { position ->
                        adapter.notifyItemRemoved(position)
                    }
                    adapter.refresh()
                    progressDialog.dismiss()
                }
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    private fun updateFileRubbish(rubbish: Boolean) {
        //取消多选状态
        setMultiSelect(false)
        if (adapter.checkedSet.isEmpty()) return
        val paths = adapter.checkedSet.map { position ->
            file.children[position].path
        }
        if (rubbish) {
            CleanPathManager.addBlackList(paths)//添加黑名单
        } else {
            CleanPathManager.addWhiteList(paths)//添加白名单
        }
        //更新文件标识及垃圾统计数据
        val notifyItems = ArrayList<Int>()
        //弹出处理进度对话框
        val progressDialog = ProgressDialog(requireActivity())
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dialog_list_file_updating))
        progressDialog.max = adapter.checkedSet.size
        progressDialog.show()
        //更新文件的协程
        CoroutinesHolder.default.launch {
            delay(500)
            adapter.checkedSet.forEach { position ->
                FileTreeManager.updateCleanState(file.children[position])
                notifyItems.add(position)
                progressDialog.incrementProgressBy(1)
            }
        }.invokeOnCompletion {//文件更新流程结束后进行刷新
            CoroutinesHolder.main.launch {
                notifyItems.forEach { position ->
                    adapter.notifyItemChanged(position)
                }
                progressDialog.dismiss()
            }
        }
    }
}