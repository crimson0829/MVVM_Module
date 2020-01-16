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

    companion object {
        /**
         * 默认的view title,可从上个页面传值
         */
        const val VIEW_TITLE = "view_title"
    }

    /**
     * 初始化titleBar， 如果是true就消费，以下的方法都会失效；默认false默认设置在BaseActivityLifecycle中实现
     */
    fun initTitleBar():Boolean

    /**
     * 初始化返回按钮图标
     */
    @DrawableRes
    fun initBackIconRes(): Int = 0

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