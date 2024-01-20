package com.bonepeople.android.sdcardcleaner

import android.graphics.drawable.ColorDrawable
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import com.bonepeople.android.base.manager.BaseApp
import com.bonepeople.android.base.view.TitleView

class App : BaseApp(), ImageLoaderFactory {
    override val appName = "SDCardCleaner"
    override fun onCreate() {
        super.onCreate()
        //页面标题栏默认配置
        TitleView.defaultConfig.apply {
            statusBarBackground = ColorDrawable(getColor(R.color.colorPrimaryDark))
            titleBarBackground = ColorDrawable(getColor(R.color.colorPrimary))
            titleColor = getColor(android.R.color.white)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
                // Coil includes two separate decoders to support decoding GIFs.
                // GifDecoder supports all API levels, but is slower.
                // ImageDecoderDecoder is powered by Android's ImageDecoder API which is only available on API 28 and above.
                // ImageDecoderDecoder is faster than GifDecoder and supports decoding animated WebP images and animated HEIF image sequences.
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }.build()
    }

    companion object {
        const val BUILD_TIME = BuildConfig.buildTime
    }
}