package com.crimson.mvvm.ext

import android.util.Log
import androidx.annotation.NonNull
import com.crimson.mvvm.BuildConfig
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * log with timber
 */
internal fun logInit(){
    if (BuildConfig.DEBUG) {
        Timber.plant(DebugTree())
    } else {
        Timber.plant(CrashReportingTree())
    }
}

/**
 * api
 */
fun logTag(msg: MESSAGE?)=Timber.tag(msg)

fun logi(msg: MESSAGE?) = Timber.i(msg)

fun logi(t: Throwable?) = Timber.i(t)

fun logd(msg: MESSAGE?) = Timber.d(msg)

fun logd(t: Throwable?) = Timber.i(t)

fun logw(msg: MESSAGE?) = Timber.w(msg)

fun logw(t: Throwable?) = Timber.i(t)

fun loge(msg: MESSAGE?) = Timber.e(msg)

fun loge(t: Throwable?) = Timber.i(t)


/** A tree which logs important information for crash reporting.  */
private class CrashReportingTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?, @NonNull message: MESSAGE,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        FakeCrashLibrary.log(
            priority,
            tag,
            message
        )
        if (t != null) {
            if (priority == Log.ERROR) {
                FakeCrashLibrary.logError(t)
            } else if (priority == Log.WARN) {
                FakeCrashLibrary.logWarning(t)
            }
        }
    }
}

/** Not a real crash reporting library!  */
private class FakeCrashLibrary private constructor() {
    companion object {

        fun log(
            priority: Int,
            tag: String?,
            message: MESSAGE?
        ) {

        }

        fun logWarning(t: Throwable?) {

        }

        fun logError(t: Throwable?) {

        }
    }


}