package com.crimson.mvvm.net.interceptor

import com.crimson.mvvm.ext.isNetConnected
import com.crimson.mvvm.net.throwable.NoConnectionException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 网络链接 拦截器
 */
class ConnectivityInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (!isNetConnected()) {
            throw NoConnectionException()
        }

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}

