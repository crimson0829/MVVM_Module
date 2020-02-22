package com.crimson.mvvm_frame.app

import android.app.Application
import com.crimson.mvvm.module.base.IModule
import com.crimson.mvvm.module.injectKoinModules

/**
 * @author crimson
 * @date   2020-02-22
 */
class AppModule :IModule{

    override fun initKoinModule() {
        injectKoinModules(
            viewModelModule,
            modelModule,
            adapterModule,
            dataModule
        )
    }

    override fun initModule(app: Application) {
    }
}