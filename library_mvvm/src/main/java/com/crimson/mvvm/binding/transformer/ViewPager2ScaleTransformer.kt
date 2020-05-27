package com.crimson.mvvm.binding.transformer

import android.os.Build
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * @author crimson
 * @date   2020-01-08
 * viewPager2 ScaleTransformer
 */
class ViewPager2ScaleTransformer(val minScale: Float = 0.85f, val defaultCenter: Float = 0.5f) :
    ViewPager2.PageTransformer {


    override fun transformPage(view: View, position: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = -abs(position)
        }
        val pageWidth = view.width
        val pageHeight = view.height

        view.pivotY = (pageHeight / 2).toFloat()
        view.pivotX = (pageWidth / 2).toFloat()
        if (position < -1) {
            view.scaleX = minScale
            view.scaleY = minScale
            view.pivotX = pageWidth.toFloat()
        } else if (position <= 1) {
            if (position < 0) {
                val scaleFactor = (1 + position) * (1 - minScale) + minScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * (defaultCenter + defaultCenter * -position)
            } else {
                val scaleFactor = (1 - position) * (1 - minScale) + minScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * ((1 - position) * defaultCenter)
            }
        } else {
            view.pivotX = 0f
            view.scaleX = minScale
            view.scaleY = minScale
        }
    }


}