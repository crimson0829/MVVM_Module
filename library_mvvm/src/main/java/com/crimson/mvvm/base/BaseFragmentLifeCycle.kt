package com.crimson.mvvm.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.crimson.mvvm.config.ViewLifeCycleManager
import com.crimson.mvvm.coroutines.ioCoroutineGlobal

/**
 * @author crimson
 * @date   2019-12-28
 * fragment lifecycle
 */
open class BaseFragmentLifeCycle : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        ViewLifeCycleManager.addFragmentToStack(f)


    }


    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        ViewLifeCycleManager.removeFragmentFromStack(f)


    }

}



