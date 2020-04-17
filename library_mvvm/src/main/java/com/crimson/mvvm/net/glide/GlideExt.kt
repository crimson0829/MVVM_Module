package com.crimson.mvvm.net.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import java.io.File


/**
 * @author crimson
 * @date   2020/4/9
 */

/**
 * 获取glide 图片
 */
fun getBitmapWithGlide(context: Context, url: String): Bitmap {

    return Glide.with(context)
        .asBitmap()
        .load(url)
        .submit(Int.MIN_VALUE, Int.MIN_VALUE)
        .get()
}