package com.crimson.mvvm.base

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.rx.bus.RxBus
import com.crimson.mvvm.rx.bus.RxCode
import com.crimson.widget.loading.EmptyLayout
import com.crimson.widget.loading.ErrorLayout
import com.crimson.widget.loading.LoadingDialog
import com.crimson.widget.loading.LoadingLayout

/**
 * @author crimson
 * @date   2019-12-29
 * 默认 view loading impl
 *
 */
class CommonViewLoading(context: Context) : IViewDataLoading {

    private val loadingView by lazy {
        LoadingLayout(context)
            .setProgressViewAttrs(AppConfigOptions.LOADING_LAYOUT_CONFIG.progressConfig)
            .setTextViewAttrs(AppConfigOptions.LOADING_LAYOUT_CONFIG.textViewConfig)

    }

    private val loadingDialog by lazy {
        LoadingDialog(context)
    }

    private val loadingError by lazy {
        ErrorLayout(context)
    }

    private val emptyView by lazy {
        EmptyLayout(context)
    }


    override fun onLoadingViewInjectToRoot(view: View?) {
        if (view is ViewGroup) {
            //如果有error view,就先移除
            if (view.contains(loadingError)) {
                view.removeView(loadingError)
            }
            if (view.contains(emptyView)) {
                view.removeView(emptyView)
            }
            if (view.contains(loadingView)) {
                view.removeView(loadingView)
            }
            view.addView(loadingView)
        }

    }

    override fun onLoadingViewResult(view: View?, needEmptyView: Boolean) {
        if (view is ViewGroup) {
            if (view.contains(loadingView)) {
                view.removeView(loadingView)
            }

            if (needEmptyView && !view.contains(emptyView)) {
                view.addView(emptyView)
            }
        }


    }

    override fun onDataLoading(message: String?) {
        //默认使用dialog实现
        loadingDialog.run {
            setMessage(message)
            show()
        }
    }

    override fun onDataLoadingResult() {
        loadingDialog.dismiss()
    }


    override fun onLoadingError(view: View?) {
        if (view is ViewGroup) {
            //先移除loading view
            if (view.contains(loadingView)) {
                view.removeView(loadingView)
            }
            //添加error view
            if (view.contains(loadingError)) {
                view.removeView(loadingError)
            }
            view.addView(loadingError)
            loadingError.setOnClickListener {
                //post rxBus，在对应的ViewModel中注册处理
                RxBus.get().post(RxCode.POST_CODE, RxCode.ERROR_LAYOUT_CLICK_CODE)
            }

        }

    }


}