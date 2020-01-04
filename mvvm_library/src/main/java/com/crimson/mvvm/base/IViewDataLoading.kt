package com.crimson.mvvm.base

import android.view.View

/**
 * @author crimson
 * @date   2019-12-29
 * data loading interface
 */
interface IViewDataLoading {

    /**
     * 获取数据时将loadingView注入到rootView中显示
     */
    fun onLoadingViewInjectToRoot(root:View?)

    /**
     * 获取数据完成时，调用该方法
     */
    fun onLoadingViewResult(root: View?)

    /**
     * 在view中获取数据的时候，调用该方法
     */
    fun onDataLoading(message:String?)

    /**
     * 在view中获取数据完成，调用该方法
     */
    fun onDataLoadingResult()

    /**
     * 加载出错时回调
     */
    fun onLoadingError(root:View?)


}