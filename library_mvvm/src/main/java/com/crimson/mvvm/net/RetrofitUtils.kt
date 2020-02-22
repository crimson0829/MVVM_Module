package com.crimson.mvvm.net

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crimson.mvvm.ext.exhaustive
import com.crimson.mvvm.ext.isNotNullOrEmpty
import com.crimson.mvvm.ext.view.toast
import com.crimson.mvvm.net.progress.ProgressListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.ByteString
import retrofit2.Response
import java.io.File
import kotlin.random.Random




/**
 *
 * 
 * handle api call dsl
 * USAGE
 *
 *
retrofitResult.handle({
//loading

}, {
//empty data

}, { // call error
message, throwable, exception ->

}, { // api error
errorBody, responseCode ->

}, {
//success handle

})
 *
 *
 */

inline fun <T> RetrofitResult<T>.handle(
        loading: () -> Unit,
        emptyData: () -> Unit,
        calError: (throwable: Throwable) -> Unit = { _ -> },
        RemoteError: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> },
        success: T.() -> Unit
) {

    when (this) {
        is RetrofitResult.Success -> {
            success.invoke(value)
        }
        RetrofitResult.Loading -> {
            loading()
        }
        RetrofitResult.EmptyData -> {
            emptyData()
        }
        is RetrofitResult.Error -> {
            calError(throwable)
        }
        is RetrofitResult.RemoteError -> {
            RemoteError(errorBody, responseCode)
        }
    }.exhaustive
}


const val multiPartContentType = "multipart/form-data"

fun HashMap<String, RequestBody>.addImagesToRetrofit(pathList: List<String>) {
    if (pathList.isNotEmpty()) {
        pathList.forEachIndexed { index, s ->
            val key = String.format("%1\$s\"; filename=\"%1\$s", "photo_" + "${index + 1}")
            this[key] = File(s).asRequestBody(multiPartContentType.toMediaType())
        }
    }
}

fun HashMap<String, RequestBody>.addImageToRetrofit(pathToFile: String?) {
    if (pathToFile.isNotNullOrEmpty()) {
        val key = String.format("%1\$s\"; filename=\"%1\$s", "photo_$randomColor")
        this[key] = File(pathToFile.toString()).asRequestBody(multiPartContentType.toMediaType())
    }
}


fun HashMap<String, RequestBody>.addImageToRetrofit(image: ByteArray?) {
    if (image != null) {
        val key = String.format("%1\$s\"; filename=\"%1\$s", "photo_$randomColor")
        this[key] = image.toRequestBody(multiPartContentType.toMediaType())
    }
}

fun HashMap<String, RequestBody>.addImageToRetrofit(image: ByteString?) {
    if (image != null) {
        val key = String.format("%1\$s\"; filename=\"%1\$s", "photo_$randomColor")
        this[key] = image.toRequestBody(multiPartContentType.toMediaType())
    }
}

/**
 * Generates a random opaque color
 * Note that this is mainly for testing
 * Should you require this method often, consider
 * rewriting the method and storing the [Random] instance
 * rather than generating one each time
 */
inline val randomColor: Int
    get() {
        val rnd = java.util.Random()
        return Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

fun HashMap<String, RequestBody>.addImageBytesToRetrofit(byteList: List<ByteArray>) {
    if (byteList.isNotEmpty()) {
        byteList.forEachIndexed { index, s ->
            val key = String.format("%1\$s\"; filename=\"%1\$s", "photo_" + "${index + 1}")
            this[key] = s.toRequestBody(multiPartContentType.toMediaType())
        }
    }
}

fun HashMap<String, RequestBody>.addImageByteStringsToRetrofit(byteList: List<ByteString>) {
    if (byteList.isNotEmpty()) {
        byteList.forEachIndexed { index, s ->
            val key = String.format("%1\$s\"; filename=\"%1\$s", "photo_" + "${index + 1}")
            this[key] = s.toRequestBody(multiPartContentType.toMediaType())
        }
    }
}


val generateRetrofitImageKeyName
    get() = String.format(
            "%1\$s\"; filename=\"%1\$s",
            "photo_${Random.nextInt(0, Int.MAX_VALUE)}"
    )

fun Double?.toRequestBodyForm(): RequestBody {
    return this.toString().toRequestBody(MultipartBody.FORM)
}

fun String?.toRequestBodyForm(): RequestBody {
    return this.toString().toRequestBody(MultipartBody.FORM)
}

fun Int?.toRequestBodyForm(): RequestBody {
    return this.toString().toRequestBody(MultipartBody.FORM)
}

fun Float?.toRequestBodyForm(): RequestBody {
    return this.toString().toRequestBody(MultipartBody.FORM)
}

fun Any?.toRequestBodyForm(): RequestBody {
    return toString().toRequestBodyForm()
}

fun <T> MutableLiveData<RetrofitResult<T>>.loading() {
    value = RetrofitResult.Loading
}

fun <T> MutableLiveData<RetrofitResult<T>>.emptyData() {
    value = RetrofitResult.EmptyData
}


fun <T> MutableLiveData<RetrofitResult<T>>.loadingPost() {
    postValue(RetrofitResult.Loading)
}

fun <T> MutableLiveData<RetrofitResult<T>>.emptyDataPost() {
    postValue(RetrofitResult.EmptyData)
}


inline fun <reified T> isGenericInstanceOf(obj: Any): Boolean = obj is T


fun <T> MutableLiveData<RetrofitResult<T>>.subscribe(response: Response<T>?, includeEmptyData: Boolean = false) {
    response?.let { serverResponse ->
        if (serverResponse.isSuccessful) {
            serverResponse.body()?.apply {
                value = if (includeEmptyData) {
                    if (this == null) {
                        RetrofitResult.EmptyData
                    } else {
                        RetrofitResult.Success(this)
                    }
                } else {
                    RetrofitResult.Success(this)
                }
            }
        } else {
            value = RetrofitResult.RemoteError(serverResponse.code(), serverResponse.errorBody())
        }
    }
}

fun <T> MutableLiveData<RetrofitResult<T>>.subscribePost(response: Response<T>?, includeEmptyData: Boolean = false) {
    response?.let { serverResponse ->
        if (serverResponse.isSuccessful) {
            serverResponse.body()?.apply {
                if (includeEmptyData) {
                    if (this == null) {
                        postValue(RetrofitResult.EmptyData)
                    } else {
                        postValue(RetrofitResult.Success(this))
                    }
                } else {
                    postValue(RetrofitResult.Success(this))
                }
            }
        } else {
            postValue(RetrofitResult.RemoteError(serverResponse.code(), serverResponse.errorBody()))
        }
    }
}


fun <T> MutableLiveData<RetrofitResult<T>>.subscribeList(response: Response<T>?, includeEmptyData: Boolean = false) {
    response?.let { serverResponse ->
        if (serverResponse.isSuccessful) {
            serverResponse.body()?.apply {
                if (includeEmptyData) {
                    if (this == null) {
                        value = RetrofitResult.EmptyData
                    } else {
                        if (this is List<*>) {
                            val list = this as List<*>
                            if (list.isNullOrEmpty()) {
                                value = RetrofitResult.EmptyData
                            } else {
                                value = RetrofitResult.Success(this)
                            }
                        } else {
                            value = RetrofitResult.Success(this)
                        }
                    }
                } else {
                    value = RetrofitResult.Success(this)
                }
            }
        } else {
            value = RetrofitResult.RemoteError(serverResponse.code(), serverResponse.errorBody())
        }
    }

}

fun <T> MutableLiveData<RetrofitResult<T>>.subscribeListPost(response: Response<T>?, includeEmptyData: Boolean = false) {
    response?.let { serverResponse ->
        if (serverResponse.isSuccessful) {
            serverResponse.body()?.apply {
                if (includeEmptyData) {
                    if (this == null) {
                        postValue(RetrofitResult.EmptyData)
                    } else {
                        if (this is List<*>) {
                            val list = this as List<*>
                            if (list.isNullOrEmpty()) {
                                postValue(RetrofitResult.EmptyData)
                            } else {
                                postValue(RetrofitResult.Success(this))
                            }
                        } else {
                            postValue(RetrofitResult.Success(this))
                        }
                    }
                } else {
                    postValue(RetrofitResult.Success(this))
                }
            }
        } else {
            postValue(RetrofitResult.RemoteError(serverResponse.code(), serverResponse.errorBody()))
        }
    }
}

fun <T> MutableLiveData<RetrofitResult<T>>.callError(throwable: Throwable) {
    value = RetrofitResult.Error(throwable)
}

fun <T> MutableLiveData<RetrofitResult<T>>.callErrorPost(throwable: Throwable) {
    postValue(RetrofitResult.Error(throwable))
}

fun <T> MutableLiveData<RetrofitResult<T>>.success(model: T) {
    value = RetrofitResult.Success(model)
}

fun <T> MutableLiveData<RetrofitResult<T>>.successPost(model: T) {
    postValue(RetrofitResult.Success(model))
}

fun <T> MutableLiveData<RetrofitResult<T>>.RemoteError(code: Int, errorBody: ResponseBody?) {
    value = RetrofitResult.RemoteError(code, errorBody)
}


fun <T> MutableLiveData<RetrofitResult<T>>.RemoteErrorPost(code: Int, errorBody: ResponseBody?) {
    postValue(RetrofitResult.RemoteError(code, errorBody))
}

//success

fun <T> MutableLiveData<RetrofitResult<T>>.onSuccess(action: (model: T) -> Unit = { _ -> }) {
    value?.let {
        when (it) {
            is RetrofitResult.Success -> {
                action(it.value)
            }
            else -> {
            }
        }
    }
}

val <T> MutableLiveData<RetrofitResult<T>>.getSuccess : T?  get() {
   return value?.let {
        when (it) {
            is RetrofitResult.Success -> {
                it.value
            }
            else -> {
                null
            }
        }
    }
}

val <T> LiveData<RetrofitResult<T>>.getSuccess: T?  get() {
    return value?.let {
        when (it) {
            is RetrofitResult.Success -> {
                it.value
            }
            else -> {
                null
            }
        }
    }
}

//Loading


fun <T> MutableLiveData<RetrofitResult<T>>.onLoading(action: () -> Unit = { }) {
    value?.let {
        when (it) {
            is RetrofitResult.Loading -> {
                action()
            }
            else -> {
            }
        }
    }
}

fun <T> LiveData<RetrofitResult<T>>.onLoading(action: () -> Unit = { }) {
    value?.let {
        when (it) {
            is RetrofitResult.Loading -> {
                action()
            }
            else -> {
            }
        }
    }
}


// Empty data


fun <T> MutableLiveData<RetrofitResult<T>>.onEmptyData(action: () -> Unit = { }) {
    value?.let {
        when (it) {
            is RetrofitResult.EmptyData -> {
                action()
            }
            else -> {
            }
        }
    }
}

fun <T> LiveData<RetrofitResult<T>>.onEmptyData(action: () -> Unit = { }) {
    value?.let {
        when (it) {
            is RetrofitResult.EmptyData -> {
                action()
            }
            else -> {
            }
        }
    }
}

// on call error on your side


fun <T> MutableLiveData<RetrofitResult<T>>.onCallError(action: (throwable: Throwable) -> Unit = { _ -> }) {
    value?.let {
        when (it) {
            is RetrofitResult.Error -> {
                action(it.throwable)
            }
            else -> {
            }
        }
    }
}

fun <T> LiveData<RetrofitResult<T>>.onCallError(action: (throwable: Throwable) -> Unit = { _ -> }) {
    value?.let {
        when (it) {
            is RetrofitResult.Error -> {
                action(it.throwable)
            }
            else -> {
            }
        }
    }
}

// on api error on server side


fun <T> MutableLiveData<RetrofitResult<T>>.onRemoteError(action: (responseCode: Int, errorBody: ResponseBody?) -> Unit = { _, _ -> }) {
    value?.let {
        when (it) {
            is RetrofitResult.RemoteError -> {
                action(it.responseCode, it.errorBody)
            }
            else -> {
            }
        }
    }
}


fun <T> LiveData<RetrofitResult<T>>.onRemoteError(action: (responseCode: Int, errorBody: ResponseBody?) -> Unit = { _, _ -> }) {
    value?.let {
        when (it) {
            is RetrofitResult.RemoteError -> {
                errorResponseCode(it.responseCode)
                action(it.responseCode, it.errorBody)
            }
            else -> {
            }
        }
    }
}

fun <T> RetrofitResult<T>.onLoading(function: () -> Unit = {}) {
    if (this is RetrofitResult.Loading) function()
}

fun <T> RetrofitResult<T>.onEmptyData(function: () -> Unit = {}) {
    if (this is RetrofitResult.EmptyData) function()
}

fun <T> RetrofitResult<T>.onCallError(function: (throwable: Throwable) -> Unit = { _ -> }) {
    if (this is RetrofitResult.Error) {
        function(throwable)
    }
}

fun <T> RetrofitResult<T>.onRemoteError(function: (errorBody: ResponseBody?, responseCode: Int) -> Unit = { _, _ -> }) {
    if (this is RetrofitResult.RemoteError) {
        function(errorBody, responseCode)
    }
}

fun <T> RetrofitResult<T>.onSuccess(function: (model: T) -> Unit = { _ -> }) {
    if (this is RetrofitResult.Success) {
        function(value)
    }
}

fun progressDSL(
    onProgressStarted: () -> Unit = {},
    onProgressFinished: () -> Unit = {},
    onProgressChanged: (percent: Int) -> Unit = { _ -> }
): ProgressListener {
    return object : ProgressListener {
        override fun onStarted() {
            onProgressStarted()
        }

        override fun onFinished() {
            onProgressFinished()
        }

        override fun onUpdate(percent: Int) {
            onProgressChanged(percent)
        }

    }
}

fun errorResponseCode(responseCode: Int) {
    when (responseCode) {

        301 -> {
            toast("Moved permanently")
        }

        400 -> {
            // bad request
            toast("Bad Request")
        }

        401 -> {
            // unauthorized
            toast("Unauthorized")
        }

        403 -> {
            toast("Forbidden")
        }

        404 -> {
            // not found
            toast("Not found")
        }

        405 -> {
            toast("Method not allowed")
        }

        406 -> {
            toast("Not acceptable")
        }

        407 -> {
            toast("Proxy authentication required")
        }

        408 -> {
            // time out
            toast("Time out")
        }

        409 -> {
            toast("Conflict error")
        }

        410 -> {
            toast("Request permanently deleted")
        }

        413 -> {
            toast("Request too large")
        }

        422 -> {
            // account exists
            toast("Account with that email already exists")
        }

        425 -> {
            toast("Server is busy")
        }

        429 -> {
            toast("Too many requests, slow down")
        }

        500 -> {
            // internal server error
            toast("Server error")
        }

        501 -> {
            toast("Not implemented")
        }

        502 -> {
            // bad gateway
            toast("Bad gateway")
        }
        504 -> {
            // gateway timeout
            toast("Gateway timeout")
        }

        511 -> {
            toast("Authentication required")
        }

        else -> {
            toast("Something went wrong, try again")
        }
    }
}




