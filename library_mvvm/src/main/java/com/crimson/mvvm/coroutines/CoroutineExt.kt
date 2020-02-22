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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import okhttp3.ResponseBody
import retrofit2.Response


suspend inline fun <T, R> T.onMain(crossinline block: (T) -> R): R {
    return withContext(Dispatchers.Main) { this@onMain.let(block) }
}

suspend inline fun <T> onMain(crossinline block: CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Main) { block.invoke(this@withContext) }
}

suspend inline fun <T, R> T.onDefault(crossinline block: (T) -> R): R {
    return withContext(Dispatchers.Default) { this@onDefault.let(block) }
}

suspend inline fun <T> onDefault(crossinline block: CoroutineScope.() -> T): T {
    return withContext(Dispatchers.Default) { block.invoke(this@withContext) }
}

suspend inline fun <T, R> T.onIO(crossinline block: (T) -> R): R {
    return withContext(Dispatchers.IO) { this@onIO.let(block) }
}

suspend inline fun <T> onIO(crossinline block: CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO) { block.invoke(this@withContext) }
}

val mainDispatcher = Dispatchers.Main
val defaultDispatcher = Dispatchers.Default
val unconfinedDispatcher = Dispatchers.Unconfined
val ioDispatcher = Dispatchers.IO


fun <T> ioCoroutineGlobal(block: suspend () -> T): Job {
    return GlobalScope.launch(Dispatchers.IO) {
        block()
    }
}

fun <T> mainCoroutineGlobal(block: suspend () -> T): Job {
    return GlobalScope.launch(Dispatchers.Main) {
        block()
    }
}

fun <T> defaultCoroutineGlobal(block: suspend () -> T): Job {
    return GlobalScope.launch(Dispatchers.Default) {
        block()
    }
}

fun <T> unconfinedCoroutineGlobal(block: suspend () -> T): Job {
    return GlobalScope.launch(Dispatchers.Unconfined) {
        block()
    }
}

suspend fun <T> withMainContext(block: suspend () -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

suspend fun <T> withIOContext(block: suspend () -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}


suspend fun <T> withDefaultContext(block: suspend () -> T): T {
    return withContext(Dispatchers.Default) {
        block()
    }
}


suspend fun <T> withUnconfinedContext(block: suspend () -> T): T {
    return withContext(Dispatchers.Unconfined) {
        block()
    }
}


/**

USAGE:

viewModelScope.launch {
callRemote(client?.getSomething(), retrofitResult)
}

 * @receiver CoroutineScope
 * @param response Response<T>?
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @return Job
 */
fun <T> CoroutineScope.callRemote(
    response: Response<T>?,
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = false
): Job {
    retrofitResult.loadingPost()
    return launch(Dispatchers.IO) {
        try {
            retrofitResult.subscribePost(response, includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }

}

fun <T> CoroutineScope.callRemoteList(
    response: Response<T>?,
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = true
): Job {
    retrofitResult.loadingPost()
    return launch(Dispatchers.IO) {
        try {
            retrofitResult.subscribeListPost(response, includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }

}


/**

USAGE:

viewModelScope.launch {
callDB(db?.getSomething(), dbResult)
}

 * @receiver CoroutineScope
 * @param queryModel Response<T>?
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @return Job
 */
fun <T> CoroutineScope.callDB(
    queryModel: T?,
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false
): Job {
    dbResult.queryingPost()
    return launch(Dispatchers.IO) {
        try {
            dbResult.subscribePost(queryModel, includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}


/**

USAGE:

viewModelScope.launch {
callDB(db?.getSomething(), dbResult)
}

 * @receiver CoroutineScope
 * @param queryModel Response<T>?
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @return Job
 */
fun <T> CoroutineScope.callDBList(
    queryModel: T?,
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true
): Job {
    dbResult.queryingPost()
    return launch(Dispatchers.IO) {
        try {
            dbResult.subscribeListPost(queryModel, includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }

}

/**
 * Android View model coroutine extensions
 * Must include the view model androidX for coroutines to provide view model scope
 */


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.apiCall()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> AndroidViewModel.callRemote(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = false,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return viewModelIOCoroutine {
        try {
            retrofitResult.subscribePost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }
}


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.callRemoteList()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> AndroidViewModel.callRemoteList(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = true,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return viewModelIOCoroutine {
        try {
            retrofitResult.subscribeListPost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> AndroidViewModel.callDB(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()
    return viewModelIOCoroutine {
        try {
            dbResult.subscribePost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

/**
 * Must include empty data
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> AndroidViewModel.callDBList(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()
    return viewModelIOCoroutine {
        try {
            dbResult.subscribeListPost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */

fun AndroidViewModel.callDB(
    onCallExecuted: () -> Unit = {},
    dbCall: suspend () -> Unit
): Job {
    return viewModelIOCoroutine {
        try {
            dbCall()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            viewModelMainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun AndroidViewModel.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Unit
): Job {
    return viewModelIOCoroutine {
        try {
            dbCall()
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            viewModelMainCoroutine {
                onCallExecuted()
            }
        }
    }
}


/**
 *
 * @receiver AndroidViewModel
 * @param action SuspendFunction0<Unit>
 * @return Job
 */
fun AndroidViewModel.viewModelIOCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return viewModelScope.launch(ioDispatcher) {
        action(this)
    }
}


/**
 *
 * @receiver AndroidViewModel
 * @param action SuspendFunction0<Unit>
 * @return Job
 */
fun AndroidViewModel.viewModelMainCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return viewModelScope.launch(mainDispatcher) {
        action(this)
    }
}


/**
 *
 * @receiver AndroidViewModel
 * @param action SuspendFunction0<Unit>
 * @return Job
 */
fun AndroidViewModel.viewModelDefaultCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return viewModelScope.launch(defaultDispatcher) {
        action(this)
    }
}

/**
 *
 * @receiver AndroidViewModel
 * @param action SuspendFunction0<Unit>
 * @return Job
 */
fun AndroidViewModel.viewModelUnconfinedCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return viewModelScope.launch(unconfinedDispatcher) {
        action(this)
    }
}


fun Fragment.ioCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(ioDispatcher) {
        action(this)
    }
}

fun Fragment.mainCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(mainDispatcher) {
        action(this)
    }
}

fun Fragment.unconfinedCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(unconfinedDispatcher) {
        action(this)
    }
}

fun Fragment.defaultCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(defaultDispatcher) {
        action(this)
    }
}


fun AppCompatActivity.ioCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(ioDispatcher) {
        action(this)
    }
}

fun AppCompatActivity.mainCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(mainDispatcher) {
        action(this)
    }
}

fun AppCompatActivity.unconfinedCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(unconfinedDispatcher) {
        action(this)
    }
}

fun AppCompatActivity.defaultCoroutine(action: suspend (scope: CoroutineScope) -> Unit = {}): Job {
    return lifecycleScope.launch(defaultDispatcher) {
        action(this)
    }
}


/**
 * Appcompat activity coroutine extensions
 * Must include the view model androidX for coroutines to provide view model scope
 */


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.apiCall()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> AppCompatActivity.callRemote(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = false,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return ioCoroutine {
        try {
            retrofitResult.subscribePost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }
}


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.callRemoteList()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> AppCompatActivity.callRemoteList(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = true,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return ioCoroutine {
        try {
            retrofitResult.subscribeListPost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> AppCompatActivity.callDB(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            dbResult.subscribePost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

/**
 * Must include empty data
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> AppCompatActivity.callDBList(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            dbResult.subscribeListPost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */

fun AppCompatActivity.callDB(
    onCallExecuted: () -> Unit = {},
    dbCall: suspend () -> Unit
): Job {
    return ioCoroutine {
        try {
            dbCall()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun AppCompatActivity.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Unit
): Job {
    return ioCoroutine {
        try {
            dbCall()
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}


/**
 * Appcompat activity coroutine extensions
 * Must include the view model androidX for coroutines to provide view model scope
 */


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.apiCall()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> Fragment.callRemote(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = false,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return ioCoroutine {
        try {
            retrofitResult.subscribePost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }
}


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.callRemoteList()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> Fragment.callRemoteList(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = true,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return ioCoroutine {
        try {
            retrofitResult.subscribeListPost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)
        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> Fragment.callDB(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            dbResult.subscribePost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

/**
 * Must include empty data
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> Fragment.callDBList(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            dbResult.subscribeListPost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */

fun Fragment.callDB(
    onCallExecuted: () -> Unit = {},
    dbCall: suspend () -> Unit
): Job {
    return ioCoroutine {
        try {
            dbCall()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun Fragment.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Unit
): Job {
    return ioCoroutine {
        try {
            dbCall()
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}


//Api call without wrappers
fun <T> AndroidViewModel.callRemote(
    apiCall: suspend () -> Response<T>?,
    onError: (throwable: Throwable) -> Unit = { _ -> },
    onUnsuccessfulCall: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> },
    onResponse: (response: T?) -> Unit
): Job {

    return viewModelIOCoroutine {
        try {
            val response = apiCall()
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


fun <T> CoroutineScope.callRemote(
    apiCall: suspend () -> Response<T>?,
    onError: (throwable: Throwable) -> Unit = { _ -> },
    onUnsuccessfulCall: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> },
    onResponse: (response: T?) -> Unit
): Job {

    return launch(ioDispatcher) {
        try {
            val response = apiCall()
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


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.apiCall()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> CoroutineScope.callRemote(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = false,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()
    return launch(ioDispatcher) {
        try {
            retrofitResult.subscribePost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)

        }
    }

}


/**
 * USAGE:
callRemote(sentResultData) {
retrofitClient?.callRemoteList()
}
 * @receiver AndroidViewModel
 * @param retrofitResult MutableLiveData<RetrofitResult<T>>
 * @param includeEmptyData Boolean
 * @param apiCall SuspendFunction0<Response<T>?>
 * @return Job
 */
fun <T> CoroutineScope.callRemoteList(
    retrofitResult: MutableLiveData<RetrofitResult<T>>,
    includeEmptyData: Boolean = true,
    apiCall: suspend () -> Response<T>?
): Job {
    retrofitResult.loadingPost()

    return launch(ioDispatcher) {
        try {
            retrofitResult.subscribeListPost(apiCall(), includeEmptyData)
        } catch (t: Throwable) {
            retrofitResult.callErrorPost(t)

        }
    }

}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> CoroutineScope.callDB(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()

    return launch(ioDispatcher) {
        try {
            dbResult.subscribePost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)

        }
    }
}

/**
 * Must include empty data
 * @receiver AndroidViewModel
 * @param dbResult MutableLiveData<DBResult<T>>
 * @param includeEmptyData Boolean
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */
fun <T> CoroutineScope.callDBList(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> T?
): Job {
    dbResult.queryingPost()

    return launch(ioDispatcher) {
        try {
            dbResult.subscribeListPost(dbCall(), includeEmptyData)
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)

        }
    }
}

/**
 * USAGE:
callRemote(dbResult) {
db?.getDBSomething()
}
 * @receiver AndroidViewModel
 * @param dbCall SuspendFunction0<T?>
 * @return Job
 */

fun CoroutineScope.callDB(
    onCallExecuted: () -> Unit = {},
    dbCall: suspend () -> Unit
): Job {

    return launch(ioDispatcher) {
        try {
            dbCall()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            launch(mainDispatcher) {
                onCallExecuted()
            }
        }
    }
}

fun CoroutineScope.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Unit
): Job {
    return launch(ioDispatcher) {
        try {
            dbCall()
        } catch (t: Throwable) {
            t.printStackTrace()
            launch(mainDispatcher) {
                onErrorAction(t)
            }
        } finally {
            launch(mainDispatcher) {
                onCallExecuted()
            }
        }
    }
}


//flow

fun <T> CoroutineScope.callDBListFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()

    return launch(ioDispatcher) {
        try {

            val result = dbCall()
            result?.collect {
                dbResult.subscribeListPost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)

        }
    }
}

fun <T> CoroutineScope.callDBFlow(
    queryModel: Flow<T>?,
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false
): Job {
    dbResult.queryingPost()
    return launch(Dispatchers.IO) {
        try {

            val call = queryModel

            call?.collect {
                dbResult.subscribePost(it, includeEmptyData)
            }

        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

fun <T> CoroutineScope.callDBListFlow(
    queryModel: Flow<T>?,
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true
): Job {
    dbResult.queryingPost()
    return launch(Dispatchers.IO) {
        try {
            val call = queryModel

            call?.collect {
                dbResult.subscribeListPost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }

}


fun <T> AndroidViewModel.callDBFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()
    return viewModelIOCoroutine {
        try {
            val flow = dbCall()
            flow?.collect {
                dbResult.subscribePost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}


fun <T> AndroidViewModel.callDBListFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()
    return viewModelIOCoroutine {
        try {
            val flow = dbCall()
            flow?.collect {
                dbResult.subscribeListPost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}


fun <T> AppCompatActivity.callDBFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            val flow = dbCall()
            flow?.collect {
                dbResult.subscribePost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}


fun <T> AppCompatActivity.callDBListFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            val flow = dbCall()
            flow?.collect {
                dbResult.subscribeListPost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}


fun <T> Fragment.callDBFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = false,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            val flow = dbCall()
            flow?.collect {
                dbResult.subscribeListPost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}


fun <T> Fragment.callDBListFlow(
    dbResult: MutableLiveData<DBResult<T>>,
    includeEmptyData: Boolean = true,
    dbCall: suspend () -> Flow<T>?
): Job {
    dbResult.queryingPost()
    return ioCoroutine {
        try {
            val flow = dbCall()
            flow?.collect {
                dbResult.subscribeListPost(it, includeEmptyData)
            }
        } catch (t: Throwable) {
            dbResult.callErrorPost(t)
        }
    }
}

// no wrappers getting the result straight up
fun <T> AndroidViewModel.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> T,
    onCalled: (model: T) -> Unit
): Job {
    return viewModelIOCoroutine {
        try {
            val call = dbCall()
            viewModelMainCoroutine {
                onCalled(call)
            }
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            viewModelMainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun <T> CoroutineScope.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> T,
    onCalled: (model: T) -> Unit
): Job {
    return launch(ioDispatcher) {
        try {
            val call = dbCall()
            launch(mainDispatcher) {
                onCalled(call)
            }
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            launch(mainDispatcher) {
                onCallExecuted()
            }
        }
    }
}

fun <T> Fragment.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> T,
    onCalled: (model: T) -> Unit
): Job {
    return ioCoroutine {
        try {
            val call = dbCall()
            mainCoroutine {
                onCalled(call)
            }
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun <T> AppCompatActivity.callDB(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> T,
    onCalled: (model: T) -> Unit
): Job {
    return ioCoroutine {
        try {
            val call = dbCall()
            mainCoroutine {
                onCalled(call)
            }
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}

// no wrappers getting the result straight up for flow

fun <T> AndroidViewModel.callDBFlow(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Flow<T>,
    onCalled: (model: T) -> Unit
): Job {
    return viewModelIOCoroutine {
        try {
            val call = dbCall()
            call.collect { model ->
                viewModelMainCoroutine {
                    onCalled(model)
                }
            }

        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            viewModelMainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun <T> CoroutineScope.callDBFlow(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Flow<T>,
    onCalled: (model: T) -> Unit
): Job {
    return launch(ioDispatcher) {
        try {
            val call = dbCall()
            call.collect {
                launch(mainDispatcher) {
                    onCalled(it)
                }
            }

        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            launch(mainDispatcher) {
                onCallExecuted()
            }
        }
    }
}

fun <T> Fragment.callDBFlow(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Flow<T>,
    onCalled: (model: T) -> Unit
): Job {
    return ioCoroutine {
        try {
            val call = dbCall()
            call.collect { model ->
                mainCoroutine {
                    onCalled(model)
                }
            }

        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}

fun <T> AppCompatActivity.callDBFlow(
    onCallExecuted: () -> Unit = {},
    onErrorAction: (throwable: Throwable) -> Unit = { _ -> },
    dbCall: suspend () -> Flow<T>,
    onCalled: (model: T) -> Unit
): Job {
    return ioCoroutine {
        try {
            val call = dbCall()
            call.collect { model ->
                mainCoroutine {
                    onCalled(model)
                }
            }
        } catch (t: Throwable) {
            onErrorAction(t)
        } finally {
            mainCoroutine {
                onCallExecuted()
            }
        }
    }
}