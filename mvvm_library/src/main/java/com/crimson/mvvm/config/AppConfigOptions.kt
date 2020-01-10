package com.crimson.mvvm.config

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import com.crimson.mvvm.base.IViewDataLoading
import com.crimson.mvvm.ext.appContext
import com.crimson.mvvm.ext.logd
import com.crimson.mvvm.net.RetrofitApi
import com.crimson.mvvm.utils.FileUtils
import com.crimson.mvvm.utils.constant.MemoryConstants
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
 * @date   2019-12-23
 * app配置类，网络链接,view初始化等设置
 * 统一管理app图片，文件等缓存路径和大小
 *
 */
class AppConfigOptions(val context: Context) {

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
            FileUtils.createOrExistsDir(it)
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
            FileUtils.createOrExistsDir(it)
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
            FileUtils.createOrExistsDir(it)
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
    fun buildLoadingViewImplClass(clazz: Class<out IViewDataLoading>): AppConfigOptions {
        LOADING_VIEW_CLAZZ = clazz
        return this
    }


    /**
     * 设置retrofit参数
     */
    fun buildRetrofit(
        config: RetrofitConfig = RetrofitConfig()
    ): AppConfigOptions {
        RetrofitApi.get(context)
            .buildOkHttpOptions(
                config.baseUrl,
                config.connectTime,
                config.showResponse,
                config.showRequest,
                config.headers
            )
        return this
    }


    /**
     * init smart refresh
     */
    fun initDefaultSmartRefresh(
        headerConfig: SmartRefreshHeaderConfig = SmartRefreshHeaderConfig(),
        footerConfig: SmartRefreshFooterConfig = SmartRefreshFooterConfig()

    ): AppConfigOptions {
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader(context).apply {
                if (headerConfig.arrowRes != 0) {
                    setArrowResource(headerConfig.arrowRes)
                }
                setTextSizeTitle(headerConfig.titleSize)
                setEnableLastTime(headerConfig.enableLastTime)
                setFinishDuration(headerConfig.finishDuration)
                minimumHeight = SmartUtil.dp2px(headerConfig.minimumHeight)
            }
        }

        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            //指定为经典Footer
            ClassicsFooter(context).apply {
                setTextSizeTitle(footerConfig.titleSize)
                setFinishDuration(footerConfig.finishDuration)
                isEnabled = footerConfig.isEnabled
                layout.setEnableLoadMore(footerConfig.enableLoadMore)
                minimumHeight = SmartUtil.dp2px(footerConfig.minimumHeight)

            }
        }

        return this
    }



    /**
     * init auto size
     */
    fun initScreenAutoSize(config: ScreenAutoSizeConfig = ScreenAutoSizeConfig()): AppConfigOptions {

        AutoSize.initCompatMultiProcess(context)
        AutoSizeConfig.getInstance()
            //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
            //如果没有这个需求建议不开启
            .setCustomFragment(config.adaptFragment)
            //是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 true, App 内的字体的大小将不会跟随系统设置中字体大小的改变
            //如果为 false, 则会跟随系统设置中字体大小的改变, 默认为 false
            .setExcludeFontScale(config.excludeFontScale)
            //是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
            .setLog(config.enableLog)
            //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
            //AutoSize 会将屏幕总高度减去状态栏高度来做适配
            //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
            .setUseDeviceSize(config.useDeviceSize)
            //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
            .setBaseOnWidth(config.baseOnWidth)
            .onAdaptListener = config.adaptListener

        return this

    }


    /**
     * 设置图片缓存路径
     */
    fun imageCachePath(file: File): AppConfigOptions {
        APP_IMAGE_CACHE_PATH = file
        FileUtils.createOrExistsDir(file)
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
        FileUtils.createOrExistsDir(file)
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
        FileUtils.createOrExistsDir(file)
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

/**
 * retrofit config
 */
data class RetrofitConfig(
    var baseUrl: String = RetrofitApi.BASE_URL,
    var connectTime: Long = 30,
    var showResponse: Boolean = true,
    var showRequest: Boolean = true,
    var headers: HashMap<String, String> = hashMapOf()
)

/**
 * 默认header设置
 */
data class SmartRefreshHeaderConfig(
    @DrawableRes var arrowRes: Int = 0,
    var titleSize: Float = 14f,
    var enableLastTime: Boolean = false,
    var finishDuration: Int = 0,
    var minimumHeight: Float = 60f
)

/**
 * 默认footer设置
 */
data class SmartRefreshFooterConfig(
    var titleSize: Float = 14f,
    var finishDuration: Int = 0,
    var isEnabled: Boolean = false,
    var enableLoadMore: Boolean = false,
    var minimumHeight: Float = 40f
)

/**
 * auto size config
 */
data class ScreenAutoSizeConfig(
    var adaptFragment: Boolean = false,
    var excludeFontScale: Boolean = false,
    var enableLog: Boolean = true,
    var useDeviceSize: Boolean = false,
    var baseOnWidth: Boolean = true,
    var adaptListener: onAdaptListener? = object : onAdaptListener {
        override fun onAdaptBefore(target: Any, activity: Activity) {
            logd(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.javaClass.name))

        }

        override fun onAdaptAfter(target: Any, activity: Activity) {
            logd(String.format(Locale.ENGLISH, "%s onAdaptAfter!", target.javaClass.name))

        }
    }
)

