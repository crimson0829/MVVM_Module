package com.crimson.mvvm.base

import org.koin.core.KoinComponent

/**
 * @author crimson
 * @date   2019-12-21
 * view impl interface
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