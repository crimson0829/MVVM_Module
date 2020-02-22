package com.crimson.mvvm.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.crimson.mvvm.module.ModuleConfig
import com.crimson.mvvm.module.appModule
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
        registerActivityLifecycleCallbacks(initActivityLifecycle())

        //koin 组件初始化
        ModuleConfig.initKoinModule()

        //koin init
        startKoin {

            androidContext(this@BaseApplication)
            androidLogger()
            modules(appModule)

        }

        //组件初始化
        ModuleConfig.initModule(this)
    }

    /**
     * 默认实现 BaseActivityLifecycle
     */
    open fun initActivityLifecycle(): ActivityLifecycleCallbacks?{
        return BaseActivityLifecycle()
    }

}