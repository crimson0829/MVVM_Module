@file:Suppress("DEPRECATION")

package com.crimson.mvvm.ext

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.crimson.mvvm.base.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author crimson
 * @date   2019-12-21
 */

typealias MESSAGE = String

/**
 * try catch
 */
inline fun <T, R> T.tryCatch(block: T.() -> R?): R? {
    return try {
        block()
    } catch (e: Throwable) {
        loge(e)
        null
    }
}

/**
 * coroutine run on IO
 */
fun <T> T.runOnIO(block: T.() -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        block()
    }
}


/**
 * get app application
 */
fun application() = BaseApplication.context as Application

/**
 * get app context
 */
fun appContext() = BaseApplication.context

/**
 * dip to px
 */
fun dp2px(dpValue: Int): Int {
    val scale = appContext()?.resources?.displayMetrics?.density ?: 0f
    return (dpValue * scale + 0.5).toInt()
}

/**
 * check net connected
 */
fun isNetConnected(): Boolean {
    val cm = appContext()?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        ?: return false
    val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}





