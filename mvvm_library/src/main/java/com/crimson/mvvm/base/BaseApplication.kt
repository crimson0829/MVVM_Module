package com.crimson.mvvm.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.crimson.mvvm.ext.logInit
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author crimson
 * @date   2019-12-21
 * base app
 */
open class BaseApplication : Application() {

    companion object {

        var context: Context? = null

    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()

        context = this
        //lifecycle
        registerActivityLifecycleCallbacks(BaseActivityLifecycle())
        //koin
        startKoin {

            androidContext(this@BaseApplication)
            androidLogger()
            modules(appModule)

        }

        //other init
        logInit()


    }


}