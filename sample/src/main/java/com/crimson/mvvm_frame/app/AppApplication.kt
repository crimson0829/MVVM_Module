package com.crimson.mvvm_frame.app

import android.graphics.Color
import android.view.Gravity
import com.crimson.mvvm.base.BaseApplication
import com.crimson.mvvm.base.CommonViewLoading
import com.crimson.mvvm.config.*
import com.crimson.mvvm_frame.R
import com.crimson.mvvm_frame.model.AndroidService
import com.crimson.widget.loading.LoadingLayoutProgressViewAttrs
import com.crimson.widget.loading.LoadingLayoutTextViewAttrs

/**
 * @author crimson
 * @date   2019-12-31
 */
class AppApplication : BaseApplication() {

    override fun onCreate() {

        super.onCreate()

        appConfig()

    }

    /**
     * 设置app_config，全局参数设置，包括状态栏设置，标题栏设置，加载视图设置，Retrofit设置等
     * more config will be add
     */
    private fun appConfig() {

        AppConfigOptions(this)
//            .buildStatusBar(StatusBarConfig(R.color.colorPrimary,false,100))
//            .buildTitleBar(TitleBarConfig(R.color.colorPrimary,R.drawable.app_back_icon,
//                Color.parseColor("#ffffff"),16f,true))
            .buildLoadingViewImplClass(CommonViewLoading::class.java)
            .buildRetrofit(RetrofitConfig(AndroidService.BASE_URL, 20))
            .buildGlide(GlideConfig(true, 1.5f, 1.5f, 20, 20))
            .buildToast(ToastConfig(Color.YELLOW, 18f, Color.BLUE))
            .buildLoadingLayout(
                LoadingLayoutConfig(
                    LoadingLayoutProgressViewAttrs(
                        50,
                        50,
                        Gravity.CENTER,
                        R.color.colorPrimary
                    ),
                    LoadingLayoutTextViewAttrs(
                        R.string.brvah_loading,
                        R.color.colorPrimaryDark,
                        12f,
                        Gravity.CENTER,
                        40
                    )
                )
            )
            .initDefaultSmartRefresh(SmartRefreshHeaderConfig(R.drawable.refresh_head_arrow))
            .initScreenAutoSize()


    }

    /**
     * 重写该方法，注册自己继承的ActivityLifecycle类
     */
    override fun initActivityLifecycle(): ActivityLifecycleCallbacks? {
        //继承BaseActivityLifecycle 的类
        return AppActivityLifecycle()
    }


}