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

    var progressView: LoadingProgressView? = null
    var textView: AppCompatTextView? = null

    init {

        progressView = LoadingProgressView(context)
        textView = AppCompatTextView(context)

        addView(progressView)
        addView(textView)

    }


    /**
     * 设置LoadingLayout属性
     */
    fun setProgressViewAttrs(attrs: LoadingLayoutProgressViewAttrs = LoadingLayoutProgressViewAttrs()): LoadingLayout {

        progressView?.apply {
            val lp = layoutParams
            lp.width = dp2px(attrs.width)
            lp.height = dp2px(attrs.height)
            if (lp is LayoutParams) {
                lp.gravity = attrs.gravity
            }
            layoutParams = lp
            setBarColor(ContextCompat.getColor(context, attrs.color))
            spin()

        }

        return this
    }

    /**
     * 设置textView属性
     */
    fun setTextViewAttrs(attrs: LoadingLayoutTextViewAttrs = LoadingLayoutTextViewAttrs()): LoadingLayout {
        textView?.apply {
            text = context.resources.getString(attrs.text)
            setTextColor(ContextCompat.getColor(context, attrs.color))
            textSize = attrs.textSize
            gravity = attrs.gravity
            val lp = layoutParams
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            if (lp is LayoutParams) {
                lp.gravity = Gravity.CENTER
                lp.topMargin = dp2px(attrs.topMargin)
            }
            layoutParams = lp

        }

        return this
    }


    /**
     * dip to px
     */
    private fun dp2px(dpValue: Int): Int {
        val scale = context?.resources?.displayMetrics?.density ?: 0f
        return (dpValue * scale + 0.5).toInt()
    }
}

/**
 * ProgressView属性
 */
data class LoadingLayoutProgressViewAttrs(
    var width: Int = 40,
    var height: Int = 40,
    var gravity: Int = Gravity.CENTER,
    var color: Int = R.color.widget_colorPrimary

)

data class LoadingLayoutTextViewAttrs(
    var text: Int = R.string.widget_loading,
    var color: Int = R.color.widget_text_color,
    var textSize: Float = 14f,
    var gravity: Int = Gravity.CENTER,
    var topMargin: Int = 40

)