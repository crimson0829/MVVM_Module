package com.crimson.widget.loading

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.crimson.widget.R

/**
 * @author crimson
 * @date   2019-12-30
 * a loading view when activity or fragment onCreate
 */
class LoadingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {

        val progressView = LoadingProgressView(context)
        val textView = AppCompatTextView(context)

        addView(progressView)
        addView(textView)

        layoutParams= LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        progressView.apply {
            val lp = layoutParams
            lp.width = dp2px(40)
            lp.height = dp2px(40)
            if (lp is LayoutParams){
                lp.gravity = Gravity.CENTER
            }
            layoutParams = lp
            setBarColor(ContextCompat.getColor(context, R.color.widget_colorPrimary))
            spin()

        }

        textView.apply {
            text = context.resources.getString(R.string.widget_loading)
            setTextColor(ContextCompat.getColor(context, R.color.widget_text_color))
            textSize = 14f
            gravity = Gravity.CENTER
            val lp = layoutParams
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            if (lp is LayoutParams){
                lp.gravity = Gravity.CENTER
                lp.topMargin = dp2px(40)
            }
            layoutParams = lp

        }


    }

    /**
     * dip to px
     */
    private fun dp2px(dpValue: Int): Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5).toInt()
    }
}