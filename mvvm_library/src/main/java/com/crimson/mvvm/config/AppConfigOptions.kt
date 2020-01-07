package com.crimson.mvvm.config

import android.app.Activity
import android.content.Context
import com.crimson.mvvm.base.IViewDataLoading
import com.crimson.mvvm.ext.appContext
import com.crimson.mvvm.ext.logd
import com.crimson.mvvm.net.RetrofitApi
import com.crimson.mvvm.utils.constant.MemoryConstants
import com.facebook.stetho.Stetho
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.util.SmartUtil
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import java.io.File
import java.util.*

/**
 * @author crimson
 * @date   2020-12-23
 * app配置类，网络链接,view初始化等设置
 * 统一管理app图片，文件等缓存路径和大小
 *
 */
class AppConfigOptions {

    companion object {

        /**
         * loading View 的实现class
         */
        var LOADING_VIEW_CLAZZ: Class<out IViewDataLoading>? = null

        /**
         * 默认图片缓存路径
         */
        var APP_IMAGE_CACHE_PATH = appContext()?.let {
            File(it.cacheDir, "app_image_cache")
        }.also {
            mkDirs(it)
        }

        /**
         * 默认图片缓存大小
         */
        var APP_IMAGE_CACHE_SIZE = 200L * MemoryConstants.MB

        /**
         * 默认文件缓存路径
         */
        var APP_FILE_CACHE_PATH = appContext()?.let {
            File(it.cacheDir, "app_file_cache")
        }.also {
            mkDirs(it)
        }
        /**
         * 默认文件缓存大小
         */
        var APP_FILE_CACHE_SIZE = 200L * MemoryConstants.MB

        /**
         * 默认http缓存路径
         */
        var APP_HTTP_CACHE_PATH = appContext()?.let {
            File(it.cacheDir, "app_http_cache")
        }.also {
            mkDirs(it)
        }
        /**
         * 默认http缓存大小
         */
        var APP_HTTP_CACHE_SIZE = 20L * MemoryConstants.MB


    }

    /**
     * 设置App的全局LoadingView实现类
     * 如果不设置，就使用默认实现类
     */
    fun buildAppLoadingViewImplClass(clazz: Class<out IViewDataLoading>): AppConfigOptions {
        LOADING_VIEW_CLAZZ = clazz
        return this
    }


    /**
     * 设置retrofit参数
     */
    fun buildRetrofit(
        context: Context, baseUrl: String, connectTime: Long = 30,
        showResponse: Boolean = true, showRequest: Boolean = true,
        headers: HashMap<String, String> = hashMapOf()
    ): AppConfigOptions {
        RetrofitApi.get(context)
            .rebuildOkHttpOptions(baseUrl,connectTime, showResponse, showRequest, headers)
        return this
    }

    /**
     * init stetho with debug
     */
    fun initStetho(context: Context): AppConfigOptions {
        Stetho.initializeWithDefaults(context)
        return this
    }

    /**
     * init smart refresh
     */
    fun initDefaultSmartRefresh(): AppConfigOptions {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context).apply {
                setTextSizeTitle(14f)
                setEnableLastTime(false)
                setFinishDuration(0)
                minimumHeight = SmartUtil.dp2px(60f)
            }
        }

        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            //指定为经典Footer
            ClassicsFooter(context).apply {
                setTextSizeTitle(14f)
                minimumHeight = SmartUtil.dp2px(40f)
                setFinishDuration(0)
                isEnabled = false
                layout.setEnableLoadMore(false)
            }
        }

        return this
    }


    /**
     * init auto size
     */
    fun initAppScreenAutoSize(context: Context): AppConfigOptions {

        AutoSize.initCompatMultiProcess(context)
        AutoSizeConfig.getInstance()
            //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启
            .setCustomFragment(true)
            .onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(
                target: Any,
                activity: Activity
            ) {
                logd(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.javaClass.name))
            }

            override fun onAdaptAfter(target: Any, activity: Activity) {
                logd(String.format(Locale.ENGLISH, "%s onAdaptAfter!", target.javaClass.name))
            }
        }

        return this

    }


    /**
     * 设置图片缓存路径
     */
    fun imageCachePath(file: File): AppConfigOptions {
        APP_IMAGE_CACHE_PATH = file
        mkDirs(file)
        return this
    }

    /**
     * 设置图片缓存大小
     */
    fun imageCacheSize(size: Long): AppConfigOptions {
        APP_IMAGE_CACHE_SIZE = size
        return this
    }

    /**
     * 设置文件缓存路径
     */
    fun fileCachePath(file: File): AppConfigOptions {
        APP_FILE_CACHE_PATH = file
        mkDirs(file)
        return this
    }

    /**
     * 设置文件缓存大小
     */
    fun fileCacheSize(size: Long): AppConfigOptions {
        APP_FILE_CACHE_SIZE = size
        return this
    }

    /**
     * 设置http缓存路径
     */
    fun httpCachePath(file: File): AppConfigOptions {
        APP_HTTP_CACHE_PATH = file
        mkDirs(file)
        return this
    }

    /**
     * 设置http缓存大小
     */
    fun httpCacheSize(size: Long): AppConfigOptions {
        APP_HTTP_CACHE_SIZE = size
        return this
    }


}


private fun mkDirs(file: File?) {
    file?.apply {
        if (!exists()) {
            mkdirs()
        }
    }
}
