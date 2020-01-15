package com.crimson.mvvm.base

import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes

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
    @DrawableRes
    fun initBackIconRes(): Int?

    /**
     * 初始化标题
     */
    fun initTitleText(): CharSequence?

    /**
     * 是否使标题居中,默认为false
     */
    fun isTitleTextCenter(): Boolean

    /**
     * 初始化menu
     */
    @MenuRes
    fun initMenuRes(): Int?

    /**
     * menu条目选中
     */
    fun onMenuItemSelected(item: MenuItem)

}