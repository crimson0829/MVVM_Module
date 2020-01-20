package com.crimson.mvvm.rx.sp

import android.content.Context
import android.content.SharedPreferences
import com.crimson.mvvm.ext.appContext

/**
 * @author crimson
 * @date   2020-01-19
 * 获取单例 SP
 */
object SPreferences {

    var PREF_NAME = "app_preferences_file"

    private var sp: SharedPreferences? = null

    fun get(): SharedPreferences? {
        if (sp == null) {
            appContext()?.let {
                sp = it.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            }
        }
        return sp
    }
}
