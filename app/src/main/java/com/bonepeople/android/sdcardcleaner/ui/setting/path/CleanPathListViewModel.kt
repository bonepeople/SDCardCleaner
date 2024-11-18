package com.bonepeople.android.sdcardcleaner.ui.setting.path

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonepeople.android.base.util.CoroutineExtension.launchOnIO
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager
import kotlinx.coroutines.flow.MutableStateFlow

class CleanPathListViewModel(val mode: CleanPathListFragment.Mode) : ViewModel() {
    val listData = MutableStateFlow<List<String>>(emptyList())
    val status = MutableStateFlow<Status>(Status.Nothing)

    init {
        viewModelScope.launchOnIO {
            listData.value = when (mode) {
                CleanPathListFragment.Mode.White -> CleanPathManager.whiteList
                CleanPathListFragment.Mode.Black -> CleanPathManager.blackList
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