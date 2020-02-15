package com.crimson.mvvm.binding

import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.crimson.mvvm.binding.consumer.BindConsumer

/**
 * @author crimson
 * @date   2020-02-14
 * SwitchCompat 扩展
 */
@BindingAdapter("app:checkChanged")
fun SwitchCompat.setCheckedListener(consumer: BindConsumer<Boolean>?) {
    consumer?.apply {
        setOnCheckedChangeListener { _, isChecked ->
            consumer.accept(isChecked)

        }
    }

}