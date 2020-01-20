@file:Suppress("DEPRECATION")

package com.crimson.mvvm.ext.component

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle

/**
 * Context 扩展
 */

inline fun <reified T : Activity> Context.startActivity(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    bundle?.let {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

inline fun <reified T : Service> Context.startService(bundle: Bundle? = null) {
    val intent = Intent(this, T::class.java)
    bundle?.let {
        intent.putExtras(bundle)
    }
    startService(intent)
}

/**
 * 是否主进程
 */
fun Context.isMainProcess(): Boolean {
    return applicationContext.packageName == getCurrProcessName()
}

/**
 * 获取当前进程名称
 */
fun Context.getCurrProcessName(): String {
    val manager =
        applicationContext.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager

    for (processInfo in manager.runningAppProcesses) {
        if (processInfo.pid == android.os.Process.myPid()) {
            return processInfo.processName
        }
    }
    return ""
}


val Context.versionCode: Long
    get() = with(packageManager.getPackageInfo(packageName, 0)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) longVersionCode else versionCode.toLong()
    }

val Context.versionName: String
    get() = packageManager.getPackageInfo(packageName, 0).versionName


fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.share(text: String, subject: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, null))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false

}


@SuppressLint("MissingPermission")
fun Context.callPhone(number: String): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.sendSMS(number: String, text: String = ""): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
        intent.putExtra("sms_body", text)
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.isNightMode() =
    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES


fun Context.dp2px(dpValue: Float): Int {
    return (dpValue * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.dp2px(dpValue: Int): Int {
    return (dpValue * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.px2dp(pxValue: Int): Float {
    return pxValue / resources.displayMetrics.density + 0.5f
}

fun Context.px2dp(pxValue: Float): Float {
    return pxValue / resources.displayMetrics.density + 0.5f
}
