package com.crimson.mvvm.base

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.crimson.mvvm.R
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.config.ViewLifeCycleManager
import com.crimson.mvvm.ext.Api
import com.crimson.mvvm.ext.afterApi
import com.crimson.mvvm.ext.runOnIO
import com.crimson.mvvm.rx.bus.RxBus
import com.crimson.mvvm.rx.bus.RxCode
import com.crimson.mvvm.utils.StatusBarUtils


/**
 * activity lifecycle
 * 全局的activity生命周期回调，可以在这里做很多事情
 *
 */
open class BaseActivityLifecycle : ActivityLifecycleCallbacks {

    private val fragmentLifeCycle = BaseFragmentLifeCycle()

    private var foregroundCount = 0
    private var configCount = 0

    /**
     * 判断app是否处于后台 false为前台，并通过RxBus发送通知
     */
    private var isBackground = false

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        //将activity添加到全局管理器
        addActivityToManager(activity)
        //初始化全局默认状态栏
        initDefaultStatusBar(activity)
        //初始化全局默认标题栏
        initDefaultTitleBar(activity)
        //初始化全局contentView函数调用
        initDefaultView(activity)



    }

    override fun onActivityStarted(activity: Activity) {

        if (configCount < 0) {
            ++configCount
        } else {
            ++foregroundCount
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (isBackground) {
            isBackground = false
            RxBus.get().post(RxCode.APP_ISBACKGROUND,isBackground)
        }
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        if (activity.isChangingConfigurations) {
            --configCount
        } else {
            --foregroundCount
            if (foregroundCount <= 0) {
                isBackground = true
                RxBus.get().post(RxCode.APP_ISBACKGROUND,isBackground)
            }

        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {

        removeActivityFromManager(activity)

    }


    /**
     * 将activity添加到全局管理器
     */
    private fun addActivityToManager(activity: Activity) {
        runOnIO {
            //添加activity入栈
            ViewLifeCycleManager.addActivityToStack(activity)
            if (activity is FragmentActivity) {
                //注册fragment生命周期
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    fragmentLifeCycle,
                    true
                )
            }
        }
    }

    /**
     * 将activity在全局管理器中移除
     */
    private fun removeActivityFromManager(activity: Activity) {
        runOnIO {
            ViewLifeCycleManager.removeActivityFromStack(activity)
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                    fragmentLifeCycle
                )
            }
        }
    }


    /**
     * 初始化默认状态栏
     * 6.0以上为默认模式默认为底色为白色的亮色模式
     * 这种情况下是状态栏可见且会导致布局上移，顶层view还需设置 android:fitsSystemWindows="true"
     */
    private fun initDefaultStatusBar(activity: Activity) {

        if (activity is IStatusBar) {

            if (activity.initStatusBar()) {
                return
            }

            if (afterApi(Api.M)) {
                StatusBarUtils.setColor(
                    activity,
                    ContextCompat.getColor(activity, AppConfigOptions.STATUS_BAR_CONFIG.bgColor),
                    AppConfigOptions.STATUS_BAR_CONFIG.bgAlpha
                )
                if (AppConfigOptions.STATUS_BAR_CONFIG.isLightMode) {
                    StatusBarUtils.setLightMode(activity)
                }
                //获取contentView设置fitsSystemWindows
                activity.findViewById<FrameLayout>(android.R.id.content)?.getChildAt(0)
                    ?.fitsSystemWindows = true
            } else {
                StatusBarUtils.setColor(
                    activity,
                    ContextCompat.getColor(activity, AppConfigOptions.STATUS_BAR_CONFIG.bgColor),
                    68//小于6.0默认68 alpha
                )
            }

        }

    }

    /**
     * 初始化默认标题栏
     *
     */
    private fun initDefaultTitleBar(activity: Activity) {

        if (activity is ITitleBar) {

            if (activity.initTitleBar()) {
                return
            }

            activity.findViewById<Toolbar>(R.id.title_bar)?.run {
                //设置toolBar为ActionBar
                if (activity is AppCompatActivity) {
                    activity.setSupportActionBar(this)
                    //设置背景色
                    setBackgroundColor(
                        ContextCompat.getColor(
                            activity,
                            AppConfigOptions.TITLE_BAR_CONFIG.bgColor
                        )
                    )

                    if (activity.initBackIconRes() != 0) {
                        //设置activity中的返回图标
                        setNavigationIcon(activity.initBackIconRes())

                    } else {
                        //默认为系统图标
                        //左侧添加一个默认的返回图标
                        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        //设置返回键可用
                        activity.supportActionBar?.setHomeButtonEnabled(true)
                    }

                    setNavigationOnClickListener {
                        activity.finish()
                    }

                    //不显示默认标题，自己添加
                    activity.supportActionBar?.setDisplayShowTitleEnabled(false)

                }
            }

            //设置title
            activity.findViewById<AppCompatTextView>(R.id.title_bar_text)?.run {

                val titleText = activity.initTitleText()
                text = titleText

                //字体颜色
                setTextColor(AppConfigOptions.TITLE_BAR_CONFIG.titleColor)
                //字体大小
                textSize = AppConfigOptions.TITLE_BAR_CONFIG.titleSize

                if (activity.isTitleTextCenter()) {
                    //如果是居中显示，就要标题左右对称
                    post {
                        val lp = layoutParams
                        lp.width = this.right - this.left * 2
                        layoutParams = lp
                        this.gravity = Gravity.CENTER
                    }
                }

            }


            /**
             * 如果想添加menu，需在Activity中重写initMenuRes 和 onMenuItemSelected
             */

        }
    }

    /**
     * 初始化全局contentView函数调用
     */
    private fun initDefaultView(activity: Activity) {
        if (activity is IView) {
            activity.initView()
            activity.initData()
            activity.initViewObservable()
        }
    }


}


