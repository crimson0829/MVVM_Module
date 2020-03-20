@file:Suppress("DEPRECATION")

package com.crimson.mvvm.ext

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.crimson.mvvm.base.BaseApplication
import com.crimson.mvvm.config.ViewLifeCycleManager
import com.crimson.mvvm.utils.ConvertUtils
import com.crimson.mvvm.utils.GsonUtils
import com.crimson.mvvm.utils.NetWorkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


/**
 * @author crimson
 * @date   2019-12-21
 * App扩展函数
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


val <T> T.exhaustive: T
    get() = this


/**
 * Check if is Main Thread.
 */
inline val isMainThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()


/**
 * Extension method to run block of code after specific Delay.
 */
fun runDelayed(delay: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, action: () -> Unit) {
    Handler().postDelayed(action, timeUnit.toMillis(delay))
}

/**
 * Extension method to run block of code on UI Thread after specific Delay.
 */
fun runDelayedOnUiThread(
    delay: Long,
    timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    action: () -> Unit
) {
    ContextHandler.handler.postDelayed(action, timeUnit.toMillis(delay))
}

/**
 * Provides handler and mainThreadScheduler.
 */
private object ContextHandler {
    val handler = Handler(Looper.getMainLooper())
    val mainThread = Looper.getMainLooper().thread
}

/**
 * 当前线程
 */
fun currentThread() = Thread.currentThread()


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

/**
 * get CurrentTimeInMillis from System.currentTimeMillis
 */
inline val currentTimeMillis: Long get() = System.currentTimeMillis()


/**
 * json 转换对象
 */
fun <T> String.json(clazz: Class<T>): T = GsonUtils.fromJson(this, clazz)

/**
 * 退出app
 */
fun exitApp() {

    ViewLifeCycleManager.closeAllActivity()
    exitProcess(0)
}









