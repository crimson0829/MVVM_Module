package com.crimson.mvvm.coroutines

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.crimson.mvvm.database.*
import com.crimson.mvvm.net.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * 协程扩展
 */
fun <T> CoroutineScope.callRemoteAsync(
    response: Response<T>?,
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = false
): Job {

    return launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()
            try {
                val task = async(ioDispatcher) {
                    response
                }
                retrofitResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }
    }
}


fun <T> CoroutineScope.callRemoteListAsync(
        response: Response<T>?,
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = true
): Job {
    return launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()
            try {
                val task = async(ioDispatcher) {
                    response
                }
                retrofitResult.subscribeListPost(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }
    }
}


fun <T> CoroutineScope.callDBAsync(
    queryModel: T?,
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false
): Job {

    return launch(mainDispatcher) {
        supervisorScope {
            dbResult.querying()

            try {
                val task = async(ioDispatcher) {
                    queryModel
                }
                dbResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                dbResult.callError(t)
            }
        }
    }
}

fun <T> CoroutineScope.callDBListAsync(
    queryModel: T?,
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true
): Job {
    return launch(mainDispatcher) {
        supervisorScope {
            dbResult.querying()

            try {
                val task = async(ioDispatcher) {
                    queryModel
                }
                dbResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                dbResult.callError(t)
            }
        }
    }
}


fun <T> AndroidViewModel.callRemoteAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = false,
        apiCall: suspend () -> Response<T>?): Job {

    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()
            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }
    }
}


fun <T> AndroidViewModel.callRemoteListAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = true,
        apiCall: suspend () -> Response<T>?): Job {
    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()
            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }
    }
}


fun <T> AndroidViewModel.callDBAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?): Job {
    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            dbResult.querying()
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                dbResult.callError(t)
            }
        }
    }
}

fun <T> AndroidViewModel.callDBListAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?): Job {
    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            dbResult.querying()
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                dbResult.callError(t)
            }
        }
    }
}

fun AndroidViewModel.callDBAsync(
        onCallExecuted: () -> Unit = {},
        dbCall: suspend () -> Unit): Job {

    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                onCallExecuted()
            }
        }
    }
}

fun AndroidViewModel.callDBAsync(
        onCallExecuted: () -> Unit = {},
        onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
        dbCall: suspend () -> Unit): Job {

    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
                onErrorAction(t)
            } finally {
                onCallExecuted()
            }
        }
    }
}

fun <T> AppCompatActivity.callRemoteAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = false,
        apiCall: suspend () -> Response<T>?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()

            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }

    }
}

fun <T> AppCompatActivity.callRemoteListAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = true,
        apiCall: suspend () -> Response<T>?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()

            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }

    }
}

fun <T> AppCompatActivity.callDBAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?): Job {

    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribe(task.await(), includeEmptyData)

            } catch (t: Throwable) {
                t.printStackTrace()
                dbResult.callError(t)
            }
        }
    }
}


fun <T> AppCompatActivity.callDBListAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribeList(task.await(), includeEmptyData)

            } catch (t: Throwable) {
                t.printStackTrace()
                dbResult.callError(t)
            }
        }
    }
}


fun AppCompatActivity.callDBAsync(
        onCallExecuted: () -> Unit = {},
        dbCall: suspend () -> Unit): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                onCallExecuted()
            }
        }
    }
}


fun AppCompatActivity.callDBAsync(
        onCallExecuted: () -> Unit = {},
        onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
        dbCall: suspend () -> Unit): Job {

    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
                onErrorAction(t)
            } finally {
                onCallExecuted()
            }
        }
    }
}

fun <T> Fragment.callRemoteAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = false,
        apiCall: suspend () -> Response<T>?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()

            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }
    }
}


fun <T> Fragment.callRemoteListAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = true,
        apiCall: suspend () -> Response<T>?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()

            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }

    }
}


fun <T> Fragment.callDBAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribe(task.await(), includeEmptyData)

            } catch (t: Throwable) {
                t.printStackTrace()
                dbResult.callError(t)
            }
        }
    }
}


fun <T> Fragment.callDBListAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribeList(task.await(), includeEmptyData)

            } catch (t: Throwable) {
                t.printStackTrace()
                dbResult.callError(t)
            }
        }
    }
}


fun Fragment.callDBAsync(
        onCallExecuted: () -> Unit = {},
        dbCall: suspend () -> Unit): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                onCallExecuted()
            }
        }
    }
}

fun Fragment.callDBAsync(
        onCallExecuted: () -> Unit = {},
        onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
        dbCall: suspend () -> Unit): Job {
    return lifecycleScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
                onErrorAction(t)
            } finally {
                onCallExecuted()
            }
        }
    }
}


//Api call without wrappers
fun <T> AndroidViewModel.callRemoteAsync(apiCall: suspend () -> Response<T>?,
                                          onError: (throwable: Throwable) -> Unit = { _ -> },
                                          onUnsuccessfulCall: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> },
                                          onResponse: (response: T?) -> Unit
): Job {

    return viewModelScope.launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                val response = task.await()
                response?.apply {
                    if (isSuccessful) {
                        onResponse(body())
                    } else {
                        onUnsuccessfulCall(errorBody(), code())
                    }
                }
            } catch (t: Throwable) {
                onError(t)
            }
        }

    }
}


fun <T> CoroutineScope.callRemoteAsync(apiCall: suspend () -> Response<T>?,
                                        onError: (throwable: Throwable) -> Unit = { _ -> },
                                        onUnsuccessfulCall: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> },
                                        onResponse: (response: T?) -> Unit
): Job {

    return launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                val response = task.await()
                response?.apply {
                    if (isSuccessful) {
                        onResponse(body())
                    } else {
                        onUnsuccessfulCall(errorBody(), code())
                    }
                }
            } catch (t: Throwable) {
                onError(t)
            }
        }

    }
}


fun <T> CoroutineScope.callRemoteAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = false,
        apiCall: suspend () -> Response<T>?): Job {


    return launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()
            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }

        }
    }
}

fun <T> CoroutineScope.callRemoteListAsync(
        retrofitResult: MutableLiveData<RetrofitResult<T>>,
        includeEmptyData: Boolean = true,
        apiCall: suspend () -> Response<T>?): Job {
    retrofitResult.loadingPost()

    return launch(mainDispatcher) {
        supervisorScope {
            retrofitResult.loading()
            try {
                val task = async(ioDispatcher) {
                    apiCall()
                }
                retrofitResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                retrofitResult.callError(t)
            }
        }
    }

}


fun <T> CoroutineScope.callDBAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?): Job {
    return launch(mainDispatcher) {
        supervisorScope {
            dbResult.querying()
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribe(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                dbResult.callError(t)
            }
        }
    }
}


fun <T> CoroutineScope.callDBListAsync(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?): Job {
    return launch(mainDispatcher) {
        supervisorScope {
            dbResult.querying()
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                dbResult.subscribeList(task.await(), includeEmptyData)
            } catch (t: Throwable) {
                dbResult.callError(t)
            }
        }
    }
}


fun CoroutineScope.callDBAsync(
        onCallExecuted: () -> Unit = {},
        dbCall: suspend () -> Unit): Job {

    return launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
            } finally {
                onCallExecuted()
            }
        }

    }
}


fun CoroutineScope.callDBAsync(
        onCallExecuted: () -> Unit = {},
        onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
        dbCall: suspend () -> Unit): Job {
    return launch(mainDispatcher) {
        supervisorScope {
            try {
                val task = async(ioDispatcher) {
                    dbCall()
                }
                task.await()
            } catch (t: Throwable) {
                t.printStackTrace()
                onErrorAction(t)
            } finally {
                onCallExecuted()
            }
        }
    }
}