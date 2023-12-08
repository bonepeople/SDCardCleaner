package com.bonepeople.android.sdcardcleaner

import android.graphics.drawable.ColorDrawable
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.bonepeople.android.base.manager.BaseApp
import com.bonepeople.android.base.view.TitleView

class App : BaseApp() {
    override val appName = "SDCardCleaner"
    override fun onCreate() {
        super.onCreate()
        //页面标题栏默认配置
        TitleView.defaultConfig.apply {
            statusBarBackground = ColorDrawable(getColor(R.color.colorPrimaryDark))
            titleBarBackground = ColorDrawable(getColor(R.color.colorPrimary))
            titleColor = getColor(android.R.color.white)
        }
        //Coil图片加载库初始化
        val imageLoader: ImageLoader = ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }.build()
        Coil.setImageLoader(imageLoader)
    }

    companion object {
        const val BUILD_TIME = BuildConfig.buildTime
    }
}