package com.bonepeople.android.sdcardcleaner.ui.setting.test

import android.os.Environment
import com.bonepeople.android.base.viewbinding.ViewBindingFragment
import com.bonepeople.android.sdcardcleaner.databinding.FragmentTestBinding
import com.bonepeople.android.sdcardcleaner.global.utils.LogUtil
import com.bonepeople.android.widget.CoroutinesHolder
import com.bonepeople.android.widget.util.AppRandom
import com.bonepeople.android.widget.util.AppView.singleClick
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class TestFragment : ViewBindingFragment<FragmentTestBinding>() {
    override fun initView() {
        views.titleView.title = "Test"
        views.button1.singleClick { createFiles() }
    }

    private fun createFiles() {
        CoroutinesHolder.io.launch {
            loadingDialog.show()
            val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            LogUtil.app.debug(root.absolutePath)
            repeat(10) {
                val dir = File(root, AppRandom.randomString(5))
                dir.mkdirs()
                repeat(100) {
                    val file = File(dir, AppRandom.randomString(5))
                    FileOutputStream(file).use {
                        it.write(AppRandom.randomString(16).toByteArray())
                    }
                }
            }
            loadingDialog.dismiss()
        }
    }
}