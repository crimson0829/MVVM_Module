package com.crimson.mvvm.module.base

import android.app.Application

/**
 * 动态配置Application，有需要初始化的组件实现该接口，统一在主app的Application中初始化
 */
interface IModule {

    /**
     * 初始化koin
     */
    fun initKoinModule()

    /**
     * 初始化各组件
     */
    fun initModule(app: Application)

}