package com.bonepeople.android.sdcardcleaner.ui.setting.path

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonepeople.android.base.util.CoroutineExtension.launchOnIO
import com.bonepeople.android.sdcardcleaner.R
import com.bonepeople.android.sdcardcleaner.global.CleanPathManager
import com.bonepeople.android.widget.ApplicationHolder
import kotlinx.coroutines.flow.MutableStateFlow

class CleanPathListViewModel(private val mode: CleanPathListFragment.Mode) : ViewModel() {
    val title = MutableStateFlow("")
    val listData = MutableStateFlow<List<String>>(emptyList())
    val status = MutableStateFlow("")

    init {
        viewModelScope.launchOnIO {
            val basicPath = runCatching { Environment.getExternalStorageDirectory().path }.getOrDefault("")
            val pathName = ApplicationHolder.app.getString(R.string.str_path_rootFile)
            when (mode) {
                CleanPathListFragment.Mode.White -> {
                    title.value = ApplicationHolder.app.getString(R.string.caption_text_white)
                    CleanPathManager.whiteList.forEach { listData.value += it.replace(basicPath, pathName) }
                }
                CleanPathListFragment.Mode.Black -> {
                    title.value = ApplicationHolder.app.getString(R.string.caption_text_black)
                    CleanPathManager.blackList.forEach { listData.value += it.replace(basicPath, pathName) }
                }
            }
            if (listData.value.isEmpty()) {
                status.value = ApplicationHolder.app.getString(R.string.state_emptyView)
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
}