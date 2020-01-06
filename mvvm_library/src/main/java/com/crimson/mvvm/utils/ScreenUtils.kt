package com.crimson.mvvm.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * @author crimson
 * @date   2019-12-26
 */
object ScreenUtils {
    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }

    /**
     * 获取状态栏高度
     *
     * @param context 目标Context
     */
    fun getStatusBarHeight(context: Context): Int { // 获得状态栏高度
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }


    /**
     * 获取屏幕dpi
     * 1.0:mdpi 1.5:hdpi 2.0:xhdpi 2.5:xxhdpi
     * @return
     */
    fun getSceenDpi(context: Context): Float {
        return context.resources.displayMetrics.density
    }


}