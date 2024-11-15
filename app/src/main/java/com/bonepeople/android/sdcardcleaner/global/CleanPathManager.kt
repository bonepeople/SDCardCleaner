package com.bonepeople.android.sdcardcleaner.global

import com.bonepeople.android.sdcardcleaner.global.utils.CommonUtil
import com.bonepeople.android.sdcardcleaner.global.utils.DataUtil
import com.bonepeople.android.sdcardcleaner.global.utils.DataUtil.AppKey
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppGson
import kotlinx.coroutines.launch

object CleanPathManager {
    val whiteList: ArrayList<String> by lazy {
        transferData()
        AppGson.toObject(DataUtil.app.getStringSync(AppKey.WHITE_LIST, "[]"))
    }
    val blackList: ArrayList<String> by lazy {
        transferData()
        AppGson.toObject(DataUtil.app.getStringSync(AppKey.BLACK_LIST, "[]"))
    }

    // remove after 2.1.5
    private fun transferData() {
        CoroutinesHolder.io.launch {
            DataUtil.app.remove(AppKey.ALREADY_TRANSFER_CLEAN_PATH)
        }
    }

    fun addWhiteList(newList: List<String>) {
        newList.forEach {
            blackList.remove(it)
            if (!whiteList.contains(it)) {
                whiteList.add(it)
            }
        }
        whiteList.sortWith { str1, str2 -> CommonUtil.comparePath(str1, str2) }
        DataUtil.app.putStringSync(AppKey.WHITE_LIST, AppGson.toJson(whiteList))
        DataUtil.app.putStringSync(AppKey.BLACK_LIST, AppGson.toJson(blackList))
    }

    fun addBlackList(newList: List<String>) {
        newList.forEach {
            whiteList.remove(it)
            if (!blackList.contains(it)) {
                blackList.add(it)
            }
        }
        blackList.sortWith { str1, str2 -> CommonUtil.comparePath(str1, str2) }
        DataUtil.app.putStringSync(AppKey.WHITE_LIST, AppGson.toJson(whiteList))
        DataUtil.app.putStringSync(AppKey.BLACK_LIST, AppGson.toJson(blackList))
    }

    fun removeWhiteList(index: Int) {
        whiteList.removeAt(index)
        DataUtil.app.putStringSync(AppKey.WHITE_LIST, AppGson.toJson(whiteList))
    }

    fun removeBlackList(index: Int) {
        blackList.removeAt(index)
        DataUtil.app.putStringSync(AppKey.BLACK_LIST, AppGson.toJson(blackList))
    }
}