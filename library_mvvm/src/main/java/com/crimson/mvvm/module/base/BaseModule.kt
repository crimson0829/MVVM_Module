package com.crimson.mvvm.module.base

import android.app.Application
import com.crimson.mvvm.ext.logInit


/**
 * @author crimson
 * @date   2020-02-12
 * 基础组件初始化
 */
class BaseModule : IModule {

    override fun initKoinModule() {
    }

    override fun initModule(app: Application) {

        //log init
        logInit()


    }

}



