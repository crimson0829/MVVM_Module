package com.crimson.mvvm.base

import org.koin.core.KoinComponent

/**
 * @author crimson
 * @date   2019-12-21
 * view interface
 * BaseActivity 和 BaseFragment 默认实现该接口，在 BaseActivityLifecycle 做方法回调
 */
interface IView : KoinComponent {

    /**
     * 初始化页面
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 初始化界面观察者
     */
    fun initViewObservable()

}