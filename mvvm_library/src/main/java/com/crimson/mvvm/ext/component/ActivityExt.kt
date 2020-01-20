@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.crimson.mvvm.ext.component

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.crimson.mvvm.utils.ScreenUtils

/**
 * @author crimson
 * @date   2020-01-19
 * Activity 扩展
 */

inline fun FragmentActivity.fragmentTransaction(function: FragmentTransaction.() -> FragmentTransaction) {
    supportFragmentManager.beginTransaction()
        .function()
        .commitAllowingStateLoss()
}

fun FragmentActivity.switchFragment(
    showFragment: Fragment,
    @IdRes containerId: Int,
    transaction: Int = FragmentTransaction.TRANSIT_NONE
) {
    supportFragmentManager.switch(showFragment, containerId, transaction)
}


/**
 * Returns the Activity's content (root) view.
 */
val Activity.rootView: View?
    get() = findViewById(android.R.id.content)


/**
 * 隐藏toolBar
 */
fun AppCompatActivity.hideToolbar() {
    supportActionBar?.hide()
}

/**
 * 显示toolBar
 */
fun AppCompatActivity.showToolbar() {
    supportActionBar?.show()
}

fun AppCompatActivity.setupToolbar(
    toolbar: Toolbar,
    displayHomeAsUpEnabled: Boolean = true,
    displayShowHomeEnabled: Boolean = true,
    displayShowTitleEnabled: Boolean = false,
    showUpArrowAsCloseIcon: Boolean = false,
    @DrawableRes closeIconDrawableRes: Int? = null
) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
        setDisplayShowHomeEnabled(displayShowHomeEnabled)
        setDisplayShowTitleEnabled(displayShowTitleEnabled)

        if (showUpArrowAsCloseIcon && closeIconDrawableRes != null) {
            setHomeAsUpIndicator(
                AppCompatResources.getDrawable(
                    this@setupToolbar,
                    closeIconDrawableRes
                )
            )
        }
    }

}

/**
 * 设置toolBar 背景色
 */
fun AppCompatActivity.setToolbarColor(@ColorRes color: Int) {
    this.supportActionBar?.setBackgroundDrawable(
        ColorDrawable(
            ContextCompat.getColor(this, color)
        )
    )
}

/**
 * 是否竖屏
 */
fun Activity.isPortrait(): Boolean {
    return resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT
}

/**
 * 是否横屏
 */
fun Activity.isLandscape(): Boolean {
    return resources.configuration.orientation === Configuration.ORIENTATION_LANDSCAPE
}

/**
 * 设置竖屏
 */
fun Activity.setPortrait() {
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

/**
 * 设置横屏
 */
fun Activity.setLandscape() {
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

/**
 * 设置全屏
 */
fun Activity.setFullScreenMode() {
    this.window.addFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}


/**
 * Makes the Activity exit fullscreen mode.
 */
fun Activity.exitFullScreenMode() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
}


/**
 * 显示软键盘
 */
fun Activity.showKeyboard() {
    val imm: InputMethodManager? =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }
    imm?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
}

fun Activity.showKeyboard(et: EditText) {
    et.requestFocus()
    val imm: InputMethodManager? =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
}


/**
 * 隐藏软键盘
 */
fun Activity.hideKeyboard() {
    val imm: InputMethodManager? =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun Activity.screenShot(removeStatusBar: Boolean = false): Bitmap? {
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)

    val bmp = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.RGB_565)
    val canvas = Canvas(bmp)
    window.decorView.draw(canvas)

    return if (removeStatusBar) {
        val statusBarHeight = ScreenUtils.getStatusBarHeight(this)
        Bitmap.createBitmap(
            bmp,
            0,
            statusBarHeight,
            dm.widthPixels,
            dm.heightPixels - statusBarHeight
        )
    } else {
        Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Activity.screenShot(removeStatusBar: Boolean = false, listener: (Int, Bitmap) -> Unit) {

    val rect = Rect()
    windowManager.defaultDisplay.getRectSize(rect)

    if (removeStatusBar) {
        val statusBarHeight = ScreenUtils.getStatusBarHeight(this)

        rect.set(rect.left, rect.top + statusBarHeight, rect.right, rect.bottom)
    }
    val bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)

    PixelCopy.request(this.window, rect, bitmap, {
        listener(it, bitmap)
    }, Handler(this.mainLooper))
}






