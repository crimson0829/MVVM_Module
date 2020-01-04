package com.crimson.mvvm.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.crimson.mvvm.config.ViewLifeCycleExt
import com.crimson.mvvm.ext.runOnIO

/**
 * @author crimson
 * @date   2019-12-28
 * fragment lifecycle
 */
open class BaseFragmentLifeCycle : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        runOnIO {
            ViewLifeCycleExt.addFragmentToStack(f)
        }

    }


    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        runOnIO {
            ViewLifeCycleExt.removeFragmentFromStack(f)
        }

    }

}



