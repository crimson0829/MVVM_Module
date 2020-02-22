package com.crimson.mvvm.net.progress

import com.crimson.mvvm.ext.isNetConnected
import com.crimson.mvvm.net.throwable.NoConnectionException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException



class ProgressInterceptor(private val progressListener: ProgressListener?) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (!isNetConnected()) throw NoConnectionException()


        if (progressListener == null) return chain.proceed(chain.request())


        val originalResponse = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .body(originalResponse.body?.let { ProgressResponseBody(it, progressListener) })
            .build()
    }
}