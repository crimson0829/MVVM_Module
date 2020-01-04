package com.crimson.mvvm.net.interceptor

import com.crimson.mvvm.ext.logw
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

class LoggerInterceptor @JvmOverloads constructor(
    private val showResponse: Boolean,
    private val showRequest: Boolean = false
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logForRequest(request)
        val response = chain.proceed(request)
        return logForResponse(response)
    }

    private fun logForResponse(response: Response): Response {
        try { //===>response log
            val builder = response.newBuilder()
            val clone = builder.build()
            logw(
                "========response'log=======" +
                        "\nurl : " + clone.request.url +
                        "\ncode : " + clone.code +
                        "\nprotocol : " + clone.protocol +
                        "\nmessage : " + if (clone.message.isEmpty()) "" else clone.message
            )
            if (showResponse) {
                var body = clone.body
                if (body != null) {
                    val mediaType = body.contentType()
                    if (mediaType != null) {
                        if (isText(mediaType)) {
                            val resp = body.string()
                            logw("responseBody's content : $resp")
                            body = resp.toResponseBody(mediaType)
                            return response.newBuilder().body(body).build()
                        } else {
                            logw("responseBody's content : " + " maybe [file part] , too large too print , ignored!")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    private fun logForRequest(request: Request) {
        try {
            val url = request.url.toString()
            val headers = request.headers
            logw(
                "========request'log=======" +
                        "\nmethod : " + request.method +
                        "\nurl : " + url +
                        "\nheaders : " + if (headers.size > 0) headers.toString() else ""
            )
            if (showRequest) {
                val requestBody = request.body
                if (requestBody != null) {
                    val mediaType = requestBody.contentType()
                    if (mediaType != null) { //                        LogUtils.wtf("requestBody's contentType : " + mediaType.toString());
                        if (isText(mediaType)) {
                            logw(
                                "requestBody's content : " + bodyToString(
                                    request
                                )
                            )
                        } else {
                            logw("requestBody's content : " + " maybe [file part] , too large too print , ignored!")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isText(mediaType: MediaType): Boolean {
        return (mediaType.type == "application" || mediaType.subtype == "json" || mediaType.subtype == "xml" || mediaType.subtype == "html" || mediaType.subtype == "webviewhtml")
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body!!.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "something error when show requestBody."
        }
    }

}