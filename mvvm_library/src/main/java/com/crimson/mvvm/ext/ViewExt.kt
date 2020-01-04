package com.crimson.mvvm.ext

import android.view.View

fun View.gone(gone: Boolean) = with(this) {
    visibility = when (gone) {
        true -> View.GONE
        false -> View.VISIBLE
    }
}



