package com.crimson.mvvm.base

import android.view.MenuItem

/**
 * @author crimson
 * @date   2020-01-14
 * titleBar 接口
 */
interface ITitleBar {

    /**
     * 初始化titleBar
     */
    fun initTitleBar()

    /**
     * 初始化返回按钮图标
     */
    fun initBackIconRes(): Int?

    /**
     * 初始化标题
     */
    fun initTitleText(): CharSequence?

    /**
     * 初始化menu
     */
    fun initMenuRes(): Int?

    /**
     * menu条目选中
     */
    fun onMenuItemSelected(item: MenuItem)

}