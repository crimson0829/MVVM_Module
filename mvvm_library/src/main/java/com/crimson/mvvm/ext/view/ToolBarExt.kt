@file:Suppress("DEPRECATION")

package com.crimson.mvvm.ext.view

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach

/**
 * toolBar 扩展
 */
fun Toolbar.changeNavigateionIconColor(@ColorInt color: Int) {
    val drawable = this.navigationIcon
    drawable?.apply {
        mutate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }
}

fun Menu.changeMenuIconColor(@ColorInt color: Int) {
    for (i in 0 until this.size()) {
        val drawable = this.getItem(i).icon
        drawable?.apply {
            mutate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }
}

fun Toolbar.changeOverflowMenuIconColor(@ColorInt color: Int) {
    try {
        val drawable = this.overflowIcon
        drawable?.apply {
            mutate()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}


/** Performs the given action on each item in this menu. */
inline fun Menu.filter(action: (item: MenuItem) -> Boolean): List<MenuItem> {
    val filteredItems = mutableListOf<MenuItem>()
    this.forEach {
        if (action.invoke(it)) filteredItems.add(it)
    }
    return filteredItems
}