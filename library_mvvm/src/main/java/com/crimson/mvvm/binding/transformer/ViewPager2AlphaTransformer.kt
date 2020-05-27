package com.crimson.mvvm.binding.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * @author crimson
 * @date   2020/5/27
 * ViewPager2 AlphaTransformer
 */
class ViewPager2AlphaTransformer(val minAlpha: Float = 0.5f) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {

        page.scaleX = 0.999f //hack

        if (position < -1) { // [-Infinity,-1)
            page.alpha = minAlpha
        } else if (position <= 1) { // [-1,1]
            //[0，-1]
            if (position < 0) {
                //[1,min]
                val factor: Float = minAlpha + (1 - minAlpha) * (1 + position)
                page.alpha = factor
            } else { //[1，0]
                //[min,1]
                val factor: Float = minAlpha + (1 - minAlpha) * (1 - position)
                page.alpha = factor
            }
        } else { // (1,+Infinity]
            page.alpha = minAlpha
        }
    }
}