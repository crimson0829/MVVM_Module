package com.crimson.mvvm.net

import android.content.Context
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.net.cookie.CookieJarImpl
import com.crimson.mvvm.net.cookie.store.PersistentCookieStore
import com.crimson.mvvm.net.interceptor.BaseInterceptor
import com.crimson.mvvm.net.interceptor.CacheInterceptor
import com.crimson.mvvm.net.interceptor.LoggerInterceptor
import com.crimson.mvvm.net.ssl.HttpsSecurityUtils.sslSocketFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * 网络请求构建
 */
class RetrofitApi private constructor(private val context: Context) {

    companion object {

        private var BASE_URL = "https://github.com/"
        //链接时间
        private var CONNECT_TIMEOUT = 30L

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

    init {
        buildRetrofit(context)
    }

    /**
     * 构建okhttp
     */
    private fun buildOkHttp(context: Context): OkHttpClient {

        if (okHttpClient == null) {

            httpCacheDirectory?.apply {
                if (httpCache == null) {
                    httpCache = Cache(this, CACHE_FILE_SIZE)
                }
            }

            val sslParams = sslSocketFactory
            val headers = HashMap<String, String>()
            okHttpClient = OkHttpClient.Builder()
                .addInterceptor(BaseInterceptor(headers)) //基础拦截器,可添加header
                .addInterceptor(CacheInterceptor()) //缓存拦截器
                .addInterceptor(LoggerInterceptor(showResponse = true, showRequest = true)) //log打印
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .cache(httpCache) //设置缓存
                .cookieJar(CookieJarImpl(PersistentCookieStore(context))) //持久化session
                .sslSocketFactory(sslParams.sSLSocketFactory!!, sslParams.trustManager!!) //信任证书
                .hostnameVerifier(HostnameVerifier { _, _ -> true })
                .build()
        }
        return okHttpClient ?: OkHttpClient()
    }

    /**
     * 构建Retrofit
     *
     * @return
     */
    private fun buildRetrofit(context: Context): Retrofit? {
        if (retrofit == null) {
            val okHttpClient = buildOkHttp(context)
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
     * 重构okhttp
     */
    fun rebuildOkhttp(okHttpClient: OkHttpClient): RetrofitApi {
        this.okHttpClient = okHttpClient
        retrofit = buildRetrofit(context)
            ?.newBuilder()
            ?.client(okHttpClient)
            ?.build()
        return this

    }

    /**
     * 重构链接时间
     */
    fun rebuildConnectTime(time: Long = 30): RetrofitApi {
        CONNECT_TIMEOUT = time
        okHttpClient = obtainOkHttp()
            ?.newBuilder()
            ?.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            ?.readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            ?.writeTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            ?.build()
            ?.also {
                retrofit = buildRetrofit(context)
                    ?.newBuilder()
                    ?.client(it)
                    ?.build()
            }
        return this

    }

    /**
     * 重构 base_url
     */
    fun rebuildBaseURL(baseUr: String): RetrofitApi {
        BASE_URL = baseUr
        retrofit = buildRetrofit(context)
            ?.newBuilder()
            ?.baseUrl(baseUr)
            ?.build()
        return this
    }

    /**
     * 获取retrofit
     */
    fun obtainRetrofit(): Retrofit? {
        return retrofit
    }

    /**
     * 获取okHttp
     */
    fun obtainOkHttp(): OkHttpClient? {
        return okHttpClient
    }


}