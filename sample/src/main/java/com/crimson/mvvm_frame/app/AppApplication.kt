package com.crimson.mvvm_frame.app

import com.crimson.mvvm.base.BaseApplication
import com.crimson.mvvm.base.injectKoinModules
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm_frame.model.AndroidService

/**
 * @author crimson
 * @date   2019-12-31
 */
class AppApplication : BaseApplication() {

    override fun onCreate() {

        //添加新的module，必须在super前调用
        injectKoinModules(viewModelModule, modelModule, adapterModule, dataModule)

        super.onCreate()

        appConfig()

    }

    /**
     * 设置app_config
     * more config will be add
     */
    private fun appConfig() {
        AppConfigOptions()
            .buildRetrofit(this, AndroidService.BASE_URL, 20)
            .initStetho(this)
            .initDefaultSmartRefresh()
            .initAppScreenAutoSize(this)


    }


}