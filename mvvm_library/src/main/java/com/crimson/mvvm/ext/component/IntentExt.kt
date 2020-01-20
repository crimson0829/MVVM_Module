@file:Suppress("DEPRECATION")

package com.crimson.mvvm.ext.component


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * @author crimson
 * @date   2020-01-19
 * intent 扩展
 */

fun Intent.startActivity(context: Context, bundle: Bundle? = null) = context.startActivity(this, bundle)

fun Intent.startActivityForResult(activity: Activity, code: Int, options: Bundle? = null) =
    activity.startActivityForResult(this, code, options)

fun Intent.startActivityForResult(fragment: Fragment, code: Int, options: Bundle? = null) =
    fragment.startActivityForResult(this, code, options)

fun Intent.clearTask() = apply {
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
}

fun Intent.clearTop() = apply {
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}

fun Intent.clearWhenTaskReset() = apply {
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
}

fun Intent.excludeFromRecents() = apply {
    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
}

fun Intent.multipleTask() = apply {
    addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
}

fun Intent.newTask() = apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}

fun Intent.noAnimation() = apply {
    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
}

fun Intent.noHistory() = apply {
    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
}

fun Intent.singleTop() = apply {
    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
}

/**
 * Add the [Intent.FLAG_ACTIVITY_NEW_DOCUMENT] flag to the [Intent].
 *
 * @return the same intent with the flag applied.
 */
fun Intent.newDocument(): Intent = apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
    } else {
        @Suppress("DEPRECATION")
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    }
}

