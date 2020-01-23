package com.crimson.mvvm.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import com.crimson.mvvm.database.DBResult
import com.crimson.mvvm.net.RetrofitResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import retrofit2.Response



/**
 * In your view model call the function and receive a wrapped response with [RetrofitResult]


 * @param RemoteCall SuspendFunction0<Response<T>?>
 * @return LiveData<RetrofitResult<T>>?
 */
fun <T> callRemoteLiveData(RemoteCall: suspend () -> Response<T>?): LiveData<RetrofitResult<T>>? {
    return liveData {
        emit(RetrofitResult.Loading)
        try {
            subscribeRemoteCall(RemoteCall.invoke())
        } catch (t: Throwable) {
            emit(RetrofitResult.Error(t))
        }
    }
}

fun <T> callRemoteLiveDataAsync(RemoteCall: suspend () -> Response<T>?): LiveData<RetrofitResult<T>>? {
    return liveData {
        emit(RetrofitResult.Loading)
        supervisorScope {
            try {
                val res = async {
                    RemoteCall.invoke()
                }
                subscribeRemoteCall(res.await())
            } catch (t: Throwable) {
                emit(RetrofitResult.Error(t))
            }
        }
    }
}

fun <T> callRemoteLiveDataListAsync(RemoteCall: suspend () -> Response<T>?): LiveData<RetrofitResult<T>>? {
    return liveData {
        emit(RetrofitResult.Loading)
        supervisorScope {
            try {
                val res = async {
                    RemoteCall.invoke()
                }
                subscribeRemoteCallList(res.await())
            } catch (t: Throwable) {
                emit(RetrofitResult.Error(t))
            }
        }
    }
}

fun <T> callRemoteListLiveData(RemoteCall: suspend () -> Response<T>?): LiveData<RetrofitResult<T>>? {
    return liveData {
        emit(RetrofitResult.Loading)
        try {
            subscribeRemoteCallList(RemoteCall.invoke())
        } catch (t: Throwable) {
            emit(RetrofitResult.Error(t))
        }
    }
}

suspend fun <T> LiveDataScope<RetrofitResult<T>>.subscribeRemoteCall(res: Response<T>?) {
    if (res == null) {
        emit(RetrofitResult.EmptyData)
    } else {
        if (res.isSuccessful) {
            val body = res.body()
            if (body == null) {
                emit(RetrofitResult.EmptyData)
            } else {
                emit(RetrofitResult.Success(body))
            }
        } else {
            emit(RetrofitResult.RemoteError(res.code(), res.errorBody()))
        }
    }
}

suspend fun <T> LiveDataScope<RetrofitResult<T>>.subscribeRemoteCallList(res: Response<T>?) {
    if (res == null) {
        emit(RetrofitResult.EmptyData)
    } else {
        if (res.isSuccessful) {
            val body = res.body()
            if (body == null) {
                emit(RetrofitResult.EmptyData)
            } else {
                if (body is List<*>) {
                    val list = body as List<*>
                    if (list.isNullOrEmpty()) {
                        emit(RetrofitResult.EmptyData)
                    } else {
                        emit(RetrofitResult.Success(body))
                    }
                } else {
                    emit(RetrofitResult.Success(body))
                }
            }
        } else {
            emit(RetrofitResult.RemoteError(res.code(), res.errorBody()))
        }
    }
}


fun <T> callDBLiveData(RemoteCall: suspend () -> T): LiveData<DBResult<T>>? {
    return liveData {
        emit(DBResult.Querying)
        try {
            subscribeCallDB(RemoteCall.invoke())
        } catch (t: Throwable) {
            emit(DBResult.DBError(t))
        }
    }
}

suspend fun <T> LiveDataScope<DBResult<T>>.subscribeCallDB(res: T) {
    emit(DBResult.Success(res))
}

/**
 * Cancel the Job if it's active.
 */
fun Job?.cancelIfActive() {
    if (this?.isActive == true) {
        cancel()
    }
}


