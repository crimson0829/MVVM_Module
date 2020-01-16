package com.crimson.mvvm.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.crimson.mvvm.ext.appContext

/**
 * @author crimson
 * @date   2019-12-26
 */
object KeyBoardUtils {
    /**
     * 隐藏软键盘
     */
    fun hideSoftInput(view: View) {
        val imm = appContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }


    /**
     * 显示软键盘
     * 这个有时候不好使
     */
    fun showSoftInput(view: View?) {
        val imm = appContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_FORCED)

    }

    /**
     * 隐藏或显示软键盘
     */
    fun hideOrShowSoftInput() {
        val imm = appContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.toggleSoftInput(
            InputMethodManager.SHOW_IMPLICIT,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}