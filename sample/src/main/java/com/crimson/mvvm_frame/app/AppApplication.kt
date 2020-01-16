package com.crimson.mvvm_frame.app

import com.crimson.mvvm.base.BaseApplication
import com.crimson.mvvm.base.CommonViewLoading
import com.crimson.mvvm.base.injectKoinModules
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.config.RetrofitConfig
import com.crimson.mvvm.config.SmartRefreshHeaderConfig
import com.crimson.mvvm_frame.R
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

        AppConfigOptions(this)
//            .buildStatusBar(StatusBarConfig(R.color.colorPrimary,false,100))
//            .buildTitleBar(TitleBarConfig(R.color.colorPrimary,R.drawable.app_back_icon,
//                Color.parseColor("#ffffff"),16f,true))
            .buildLoadingViewImplClass(CommonViewLoading::class.java)
            .buildRetrofit(RetrofitConfig(AndroidService.BASE_URL, 20))
            .initDefaultSmartRefresh(SmartRefreshHeaderConfig(R.drawable.refresh_head_arrow))
            .initScreenAutoSize()


    }


}