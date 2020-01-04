package com.crimson.mvvm.config

import android.app.Activity
import androidx.fragment.app.Fragment
import java.util.*

/**
 * @author crimson
 * @date   2019-12-28
 * view lifecycle
 */
object ViewLifeCycleExt {

    /**
     *  app activity stack
     */
    private val activityStack = LinkedList<Activity>()

    /**
     *  app fragment stack
     */
    private val fragmentStack = LinkedList<Fragment>()

    /**
     * add from lifecycle
     */
    internal fun addActivityToStack(act: Activity) = activityStack.addFirst(act)

    /**
     * remove from lifecycle
     */
    internal fun removeActivityFromStack(act: Activity) = activityStack.remove(act)

    internal fun addFragmentToStack(fragment: Fragment) = fragmentStack.addFirst(fragment)

    internal fun removeFragmentFromStack(fragment: Fragment) = fragmentStack.remove(fragment)


    /**
     * 获取当前的 activity
     */
    fun obtainCurrentActivity(): Activity? {
        if (activityStack.isNotEmpty()) {
            return activityStack.first
        }
        return null
    }

    /**
     * 根据class获取stack中的activity
     */
    fun obainActivity(cls: Class<Activity>?): Activity? {

        cls?.apply {
            activityStack.forEach {
                if (it.javaClass == cls) {
                    return it
                }
            }
        }

        return null
    }


    /**
     * 关闭 activity
     */
    fun closeActivity(act: Activity? = null) {
        if (act == null) {
            obtainCurrentActivity()?.apply { finish() }
        } else {
            act.apply { finish() }

        }
    }

    /**
     * 关闭所有 activity
     */
    fun closeAllActivity() {
        activityStack.forEach {
            it.finish()
        }
        activityStack.clear()
    }

    /**
     * 获取当前的 fragment
     */
    fun obtainCurrentFragment(): Fragment? {
        if (fragmentStack.isNotEmpty()) {
            return fragmentStack.first
        }
        return null
    }

}