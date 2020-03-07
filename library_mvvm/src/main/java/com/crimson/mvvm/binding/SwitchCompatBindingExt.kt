package com.crimson.mvvm.binding

import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.crimson.mvvm.binding.consumer.BindBiConsumer

/**
 * @author crimson
 * @date   2020-02-14
 * SwitchCompat 扩展
 */
@BindingAdapter("app:checkChanged")
fun SwitchCompat.setCheckedListener(consumer: BindBiConsumer<CompoundButton,Boolean>?) {
    consumer?.apply {
        setOnCheckedChangeListener { buttonView, isChecked ->
            consumer.accept(buttonView,isChecked)

        }
    }

}