package com.bonepeople.android.sdcardcleaner.global

import com.bonepeople.android.sdcardcleaner.global.utils.CommonUtil
import com.bonepeople.android.sdcardcleaner.global.utils.DataUtil
import com.bonepeople.android.sdcardcleaner.global.utils.DataUtil.AppKey
import com.bonepeople.android.widget.util.AppGson
import com.bonepeople.android.widget.util.AppStorage

object CleanPathManager {
    val whiteList: ArrayList<String> by lazy {
        transferData()
        AppGson.toObject(DataUtil.app.getStringSync(AppKey.WHITE_LIST, "[]"))
    }
    val blackList: ArrayList<String> by lazy {
        transferData()
        AppGson.toObject(DataUtil.app.getStringSync(AppKey.BLACK_LIST, "[]"))
    }

    private fun transferData() {
        if (!DataUtil.app.getBooleanSync(AppKey.ALREADY_TRANSFER_CLEAN_PATH)) {
            val whiteList = AppStorage.getString(Keys.WHITE_LIST, "[]")
            val blackList = AppStorage.getString(Keys.BLACK_LIST, "[]")

            DataUtil.app.putStringSync(AppKey.WHITE_LIST, whiteList)
            DataUtil.app.putStringSync(AppKey.BLACK_LIST, blackList)

            DataUtil.app.putBooleanSync(AppKey.ALREADY_TRANSFER_CLEAN_PATH, true)
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