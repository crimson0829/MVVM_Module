package com.crimson.mvvm.net

import okhttp3.ResponseBody


/**
 * Retrofit Result 包装类，用作请求返回数据包装
 */
sealed class RetrofitResult<out T> {

    // handle UI changes when everything is loaded
    data class Success<T>(val value: T) : RetrofitResult<T>()
    // handle loading state
    object Loading : RetrofitResult<Nothing>()
    //same as no data except this one returns that no data was obtained from the server
    object EmptyData : RetrofitResult<Nothing>()
    //this one gets thrown when there's an error on your side
    data class Error(val throwable: Throwable) : RetrofitResult<Nothing>()
    //whenever the remote throws an error
    data class RemoteError(val responseCode: Int, val errorBody: ResponseBody?) : RetrofitResult<Nothing>()

}

