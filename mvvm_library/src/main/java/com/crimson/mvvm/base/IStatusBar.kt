package com.crimson.mvvm.base

/**
 * @author crimson
 * @date   2020-01-14
 * statusBar init interrface
 */
interface IStatusBar {

    /**
     * 初始化状态栏 true为消费；false为不消费，交给BaseActivityLifecycle处理
     */
    fun initStatusBar(): Boolean

}