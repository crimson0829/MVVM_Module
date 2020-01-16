package com.crimson.mvvm_frame.app

import android.app.Activity
import android.os.Bundle
import com.crimson.mvvm.base.BaseActivityLifecycle

/**
 * @author crimson
 * @date   2020-01-16
 * app AppActivityLifecycle，可自己扩展
 *
 */
class AppActivityLifecycle : BaseActivityLifecycle(){

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
    }
}