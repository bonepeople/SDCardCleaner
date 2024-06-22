package com.bonepeople.android.sdcardcleaner.ui.setting.path

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonepeople.android.base.util.CoroutineExtension.launchOnIO
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager
import com.bonepeople.android.widget.ApplicationHolder
import kotlinx.coroutines.flow.MutableStateFlow

class CleanPathListViewModel(val mode: CleanPathListFragment.Mode) : ViewModel() {
    val listData = MutableStateFlow<List<String>>(emptyList())
    val status = MutableStateFlow<Status>(Status.Nothing)

    init {
        viewModelScope.launchOnIO {
            val rootPath = runCatching { Environment.getExternalStorageDirectory().path }.getOrDefault("")
            val rootName = ApplicationHolder.app.getString(R.string.str_path_rootFile)
            when (mode) {
                CleanPathListFragment.Mode.White -> {
                    CleanPathManager.whiteList.forEach { listData.value += it.replace(rootPath, rootName) }
                }
                CleanPathListFragment.Mode.Black -> {
                    CleanPathManager.blackList.forEach { listData.value += it.replace(rootPath, rootName) }
                }
            }
            if (listData.value.isEmpty()) {
                status.value = Status.Empty
            }
        }
    }

    fun clickItem(data: String) {
        when (mode) {
            CleanPathListFragment.Mode.White -> CleanPathManager.removeWhiteList(listData.value.indexOf(data))
            CleanPathListFragment.Mode.Black -> CleanPathManager.removeBlackList(listData.value.indexOf(data))
        }
        listData.value -= data
    }

    sealed class Status {
        object Nothing : Status()
        object Empty : Status()
    }
}