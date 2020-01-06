@file:Suppress("DEPRECATION")

package com.crimson.mvvm.ext

import android.app.Application
import com.crimson.mvvm.base.BaseApplication
import com.crimson.mvvm.utils.ConvertUtils
import com.crimson.mvvm.utils.NetWorkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
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
fun <T> T.runOnIO(block: T.() -> Unit): Job {
    return GlobalScope.launch(Dispatchers.IO) {
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
    appContext()?.apply {
        return ConvertUtils.dp2px(this, dpValue.toFloat())
    }
    return 0
}

/**
 * check net connected
 */
fun isNetConnected(): Boolean {
    appContext()?.apply {
        return NetWorkUtils.isNetworkConnected(this)
    }
    return false
}




