package com.bonepeople.android.sdcardcleaner.global

import com.bonepeople.android.sdcardcleaner.utils.CommonUtil
import com.bonepeople.android.sdcardcleaner.utils.ConfigUtil
import com.bonepeople.android.widget.ApplicationHolder
import com.bonepeople.android.widget.util.AppGson
import com.bonepeople.android.widget.util.AppStorage

object CleanPathManager {
    val whiteList: ArrayList<String> by lazy {
        transferData()
        AppGson.toObject(AppStorage.getString(Keys.WHITE_LIST, "[]"))
    }
    val blackList: ArrayList<String> by lazy {
        transferData()
        AppGson.toObject(AppStorage.getString(Keys.BLACK_LIST, "[]"))
    }

    private fun transferData() {
        if (!AppStorage.getBoolean(Keys.ALREADY_TRANSFER_CLEAN_PATH)) {
            val saveList = ConfigUtil.getSaveList(ApplicationHolder.instance)
            val cleanList = ConfigUtil.getCleanList(ApplicationHolder.instance)

            if (!saveList.isNullOrEmpty()) {
                saveList.toList().sortedWith { str1, str2 -> CommonUtil.comparePath(str1, str2) }.let {
                    AppStorage.putString(Keys.WHITE_LIST, AppGson.toJson(it))
                }
            }
            if (!cleanList.isNullOrEmpty()) {
                cleanList.toList().sortedWith { str1, str2 -> CommonUtil.comparePath(str1, str2) }.let {
                    AppStorage.putString(Keys.BLACK_LIST, AppGson.toJson(it))
                }
            }
            AppStorage.putBoolean(Keys.ALREADY_TRANSFER_CLEAN_PATH, true)
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