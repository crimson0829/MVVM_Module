package com.crimson.mvvm.binding

import androidx.databinding.BindingAdapter
import com.crimson.mvvm.binding.consumer.BindBiConsumer
import com.google.android.material.appbar.AppBarLayout

/**
 * @author crimson
 * @date   2020-02-26
 * AppBarLayout 扩展
 */

/**
 *
 * 滑动绑定
 *
 */
@BindingAdapter("app:abl_scroll")
fun AppBarLayout.bindScroll(consumer: BindBiConsumer<AppBarLayout,Int>?) {

    consumer?.apply {
        addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { layout, offset ->
            consumer.accept(layout,offset)

        })
    }

}