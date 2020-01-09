package com.crimson.mvvm.net

import android.content.Context
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.net.cookie.CookieJarImpl
import com.crimson.mvvm.net.cookie.store.PersistentCookieStore
import com.crimson.mvvm.net.interceptor.BaseInterceptor
import com.crimson.mvvm.net.interceptor.CacheInterceptor
import com.crimson.mvvm.net.interceptor.LoggerInterceptor
import com.crimson.mvvm.net.ssl.HttpsSecurityUtils
import com.crimson.mvvm.net.ssl.HttpsSecurityUtils.sslSocketFactory
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 网络请求构建
 */
class RetrofitApi private constructor(private val context: Context) {

    companion object {

        //Base Url
        private var BASE_URL = "https://github.com/"

        //链接时间
        private var CONNECT_TIMEOUT = 30L

        //response log
        private var SHOW_RESPONSE_LOG = true

        //request log
        private var SHOW_REQUEST_LOG = true

        //header map
        private var HEADERS = hashMapOf<String, String>()

        //缓存文件大小
        private var CACHE_FILE_SIZE = AppConfigOptions.APP_HTTP_CACHE_SIZE

        //缓存文件路径
        private var httpCacheDirectory: File? = AppConfigOptions.APP_HTTP_CACHE_PATH

        @Volatile
        private var INSTANCE: RetrofitApi? = null

        fun get(context: Context): RetrofitApi {
            if (INSTANCE == null) {
                synchronized(RetrofitApi::class) {
                    if (INSTANCE == null) {
                        INSTANCE = RetrofitApi(context)
                    }
                }
            }
            return INSTANCE ?: RetrofitApi(context)
        }
    }

    private var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    //缓存
    private var httpCache: Cache? = null

    /**
     * 构建okhttp
     */
    private fun buildOkHttp(): OkHttpClient {

        if (okHttpClient == null) {
            okHttpClient = createOkHttpClient()
        }
        return okHttpClient ?: OkHttpClient()
    }

    /**
     * 新建okHttpClient
     */
    private fun createOkHttpClient(): OkHttpClient? {

        httpCacheDirectory?.apply {
            if (httpCache == null) {
                httpCache = Cache(this, CACHE_FILE_SIZE)
            }
        }

        val sslParams = sslSocketFactory
        return OkHttpClient.Builder()
            .addInterceptor(BaseInterceptor(HEADERS)) //基础拦截器,可添加header
            .addInterceptor(CacheInterceptor()) //缓存拦截器
            .addInterceptor(
                //log打印
                LoggerInterceptor(
                    showResponse = SHOW_RESPONSE_LOG,
                    showRequest = SHOW_REQUEST_LOG
                )
            )
            .addNetworkInterceptor(StethoInterceptor())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .cache(httpCache) //设置缓存
            .cookieJar(CookieJarImpl(PersistentCookieStore(context))) //持久化session
            .sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!) //信任证书
            .hostnameVerifier(HttpsSecurityUtils.UnSafeHostnameVerifier)
            .build()
    }

    /**
     * 构建Retrofit
     *
     * @return
     */
    private fun buildRetrofit(): Retrofit? {
        if (retrofit == null) {
            val okHttpClient = buildOkHttp()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofit

    }

    /**
     * 构建okhttp
     */
    fun buildOkHttp(okHttpClient: OkHttpClient): RetrofitApi {
        this.okHttpClient = okHttpClient
        retrofit = buildRetrofit()
            ?.newBuilder()
            ?.client(okHttpClient)
            ?.build()
        return this

    }

    /**
     * 构建okHttp参数
     */
    fun buildOkHttpOptions(
        baseUrl: String = BASE_URL,
        connectTime: Long = 30,
        showResponseLog: Boolean = true,
        showRequestLoG: Boolean = true,
        headers: HashMap<String, String> = hashMapOf()
    ): RetrofitApi {
        CONNECT_TIMEOUT = connectTime
        SHOW_RESPONSE_LOG = showResponseLog
        SHOW_REQUEST_LOG = showRequestLoG
        HEADERS = headers
        okHttpClient = createOkHttpClient()
            ?.also {
                buildOkHttp(it)
                    .buildBaseURL(baseUrl)
            }
        return this

    }

    /**
     * 构建 base_url
     */
    fun buildBaseURL(baseUrl: String): RetrofitApi {
        BASE_URL = baseUrl
        retrofit = buildRetrofit()
            ?.newBuilder()
            ?.baseUrl(baseUrl)
            ?.build()
        return this
    }

    /**
     * 获取retrofit
     */
    fun obtainRetrofit(): Retrofit? {
        if (retrofit == null) {
            buildRetrofit()
        }
        return retrofit
    }

    /**
     * 获取okHttp
     */
    fun obtainOkHttp(): OkHttpClient? {
        if (okHttpClient == null) {
            buildOkHttp()
        }
        return okHttpClient
    }


}