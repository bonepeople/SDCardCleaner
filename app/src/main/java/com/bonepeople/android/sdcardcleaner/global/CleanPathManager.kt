package com.bonepeople.android.sdcardcleaner.global

import com.bonepeople.android.sdcardcleaner.global.utils.CommonUtil
import com.bonepeople.android.widget.util.AppGson
import com.bonepeople.android.widget.util.AppStorage

object CleanPathManager {
    val whiteList: ArrayList<String> by lazy {
        AppStorage.remove(Keys.ALREADY_TRANSFER_CLEAN_PATH)
        AppGson.toObject(AppStorage.getString(Keys.WHITE_LIST, "[]"))
    }
    val blackList: ArrayList<String> by lazy {
        AppStorage.remove(Keys.ALREADY_TRANSFER_CLEAN_PATH)
        AppGson.toObject(AppStorage.getString(Keys.BLACK_LIST, "[]"))
    }

    fun addWhiteList(newList: List<String>) {
        newList.forEach {
            blackList.remove(it)
            if (!whiteList.contains(it)) {
                whiteList.add(it)
            }
        }
        whiteList.sortWith { str1, str2 -> CommonUtil.comparePath(str1, str2) }
        AppStorage.putString(Keys.WHITE_LIST, AppGson.toJson(whiteList))
        AppStorage.putString(Keys.BLACK_LIST, AppGson.toJson(blackList))
    }

    fun addBlackList(newList: List<String>) {
        newList.forEach {
            whiteList.remove(it)
            if (!blackList.contains(it)) {
                blackList.add(it)
            }
        }
        blackList.sortWith { str1, str2 -> CommonUtil.comparePath(str1, str2) }
        AppStorage.putString(Keys.WHITE_LIST, AppGson.toJson(whiteList))
        AppStorage.putString(Keys.BLACK_LIST, AppGson.toJson(blackList))
    }

    fun removeWhiteList(index: Int) {
        whiteList.removeAt(index)
        AppStorage.putString(Keys.WHITE_LIST, AppGson.toJson(whiteList))
    }

    fun removeBlackList(index: Int) {
        blackList.removeAt(index)
        AppStorage.putString(Keys.BLACK_LIST, AppGson.toJson(blackList))
    }
}