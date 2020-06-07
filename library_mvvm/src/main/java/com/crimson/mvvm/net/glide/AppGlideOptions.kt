package com.crimson.mvvm.net.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.net.NetworkClient
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * glide 配置，将请求换成 okHttp
 */
@GlideModule(glideName = "appGlide")
class AppGlideOptions : AppGlideModule() {
    override fun applyOptions(
        context: Context,
        builder: GlideBuilder
    ) {

        builder.setDiskCache {
            DiskLruCacheWrapper.create(
                AppConfigOptions.APP_IMAGE_CACHE_PATH, AppConfigOptions.APP_IMAGE_CACHE_SIZE
            )
        }
        val calculator = MemorySizeCalculator.Builder(context).build()
        val defaultMemoryCacheSize = calculator.memoryCacheSize
        val defaultBitmapPoolSize = calculator.bitmapPoolSize
        val customMemoryCacheSize =
            (AppConfigOptions.GLIDE_CONFIG.memoryCacheSizeFactor * defaultMemoryCacheSize).toInt()
        val customBitmapPoolSize =
            (AppConfigOptions.GLIDE_CONFIG.bitmapPoolSizeFactor * defaultBitmapPoolSize).toInt()

        builder.setMemoryCache(LruResourceCache(customMemoryCacheSize.toLong()))
            .setBitmapPool(LruBitmapPool(customBitmapPoolSize.toLong()))

        if (AppConfigOptions.GLIDE_CONFIG.sourceExecutorThreadCount > 0) {
            builder.setSourceExecutor(
                GlideExecutor.newSourceExecutor(
                    AppConfigOptions.GLIDE_CONFIG.sourceExecutorThreadCount,
                    "source",
                    GlideExecutor.UncaughtThrowableStrategy.DEFAULT
                )
            )
        }
        if (AppConfigOptions.GLIDE_CONFIG.diskExecutorThreadCount > 0) {
            builder.setDiskCacheExecutor(
                GlideExecutor.newDiskCacheExecutor(
                    AppConfigOptions.GLIDE_CONFIG.diskExecutorThreadCount,
                    "disk",
                    GlideExecutor.UncaughtThrowableStrategy.DEFAULT
                )
            )
        }


    }

    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(
                (if (AppConfigOptions.GLIDE_CONFIG.needUseNetworkClientOkHttp) NetworkClient.get(
                    context
                ).obtainOkHttp() else OkHttpClient()) as Call.Factory
            )
        )

    }
}