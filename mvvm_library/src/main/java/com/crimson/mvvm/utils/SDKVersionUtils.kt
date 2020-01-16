package com.crimson.mvvm.utils

import android.os.Build

/**
 * @author crimson
 * @date   2020-01-14
 * android系统版本工具
 *
 */
object SDKVersionUtils {

    val SDK_VERSION = Build.VERSION.SDK_INT

    //4.0
    val API_15 = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
    //4.1
    val API_16 = Build.VERSION_CODES.JELLY_BEAN
    //4.4
    val API_19 = Build.VERSION_CODES.KITKAT
    //5.0
    val API_21 = Build.VERSION_CODES.LOLLIPOP
    //5.1
    val API_22 = Build.VERSION_CODES.LOLLIPOP_MR1
    //6.0
    val API_23 = Build.VERSION_CODES.M
    //7.0
    val API_24 = Build.VERSION_CODES.N
    //7.1
    val API_25 = Build.VERSION_CODES.N_MR1
    //8.0
    val API_26 = Build.VERSION_CODES.O
    //8.1
    val API_27 = Build.VERSION_CODES.O_MR1
    //9.0
    val API_28 = Build.VERSION_CODES.P
    //10.0
    val API_29 = Build.VERSION_CODES.Q

    //大于置顶的sdk version
    fun isAboveSDKVersion(sdkVersion: Int): Boolean = SDK_VERSION >= sdkVersion

    //系统大于4.0
    fun isAboveAndroid4(): Boolean = SDK_VERSION >= API_15

    //系统大于4.1
    fun isAboveAndroid4D1(): Boolean = SDK_VERSION >= API_16

    //系统大于4.4
    fun isAboveAndroid4D4(): Boolean = SDK_VERSION >= API_19

    //系统大于5.0
    fun isAboveAndroid5(): Boolean = SDK_VERSION >= API_21

    //6.0以上系统
    fun isAboveAndroid6(): Boolean = SDK_VERSION >= API_23

    //7.0以上系统
    fun isAboveAndroid7(): Boolean = SDK_VERSION >= API_24

    //8.0以上系统
    fun isAboveAndroid8(): Boolean = SDK_VERSION >= API_26

    //9.0以上系统
    fun isAboveAndroid9(): Boolean = SDK_VERSION >= API_28

    //10.0以上系统
    fun isAboveAndroid10(): Boolean = SDK_VERSION >= API_29


}