package com.crimson.mvvm.module.base

import android.app.Application
import android.view.Choreographer
import com.crimson.mvvm.ext.debugMode
import com.crimson.mvvm.ext.logInit
import com.crimson.mvvm.utils.fps.FPSFrameCallback


/**
 * @author crimson
 * @date   2020-02-12
 * 基础组件初始化
 */
class BaseModule : IModule {

    override fun initKoinModule() {
    }

    override fun initModule(app: Application) {

        debugMode {
            //log init
            logInit()
            //fps 监听
            Choreographer.getInstance().postFrameCallback(FPSFrameCallback(System.nanoTime()))

        }


    }

}



