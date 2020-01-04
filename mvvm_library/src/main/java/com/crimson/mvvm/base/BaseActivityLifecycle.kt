package com.crimson.mvvm.base

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.crimson.mvvm.config.ViewLifeCycleExt
import com.crimson.mvvm.ext.runOnIO
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity

/**
 * activity lifecycle
 */
open class BaseActivityLifecycle : ActivityLifecycleCallbacks {

   private val fragmentLifeCycle = BaseFragmentLifeCycle()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        runOnIO {
            ViewLifeCycleExt.addActivityToStack(activity)
            if (activity is RxAppCompatActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifeCycle,true)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {

        runOnIO {
            ViewLifeCycleExt.removeActivityFromStack(activity)
            if (activity is RxAppCompatActivity) {
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifeCycle)
            }
        }

    }
}


