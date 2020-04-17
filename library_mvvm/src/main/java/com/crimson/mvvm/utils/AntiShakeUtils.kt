package com.crimson.mvvm.utils

import android.view.View
import androidx.annotation.NonNull
import java.util.*


/**
 * @author crimson
 * @date   2020/4/9
 * 防抖动点击,针对oppo手机rxbinding3 bug，
 */
object AntiShakeUtils {

    private const val INTERNAL_TIME: Long = 1000

    private const val LAST_CLICK_TIME = "last_click_Time"


    /**
     * Whether this click event is invalid.
     *
     * @param target       target view
     * @param internalTime the internal time. The unit is millisecond.
     * @return true, invalid click event.
     */
    fun isInvalidClick(
        @NonNull target: View,
        internalTime: Long = INTERNAL_TIME
    ): Boolean {

        val curTimeStamp = Calendar.getInstance().timeInMillis
        val tag: Any? = target.getTag(LAST_CLICK_TIME.hashCode())
        if (tag == null) {
            target.setTag(LAST_CLICK_TIME.hashCode(), curTimeStamp)
            return false
        }
        val lastClickTimeStamp = tag as Long
        val l = curTimeStamp - lastClickTimeStamp
        val isInvalid = l < internalTime
        if (!isInvalid) target.setTag(LAST_CLICK_TIME.hashCode(), curTimeStamp)
        return isInvalid
    }
}