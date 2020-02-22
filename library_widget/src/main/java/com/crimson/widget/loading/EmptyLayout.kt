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
 * @date   2020-02-21
 * 空数据布局
 */
class EmptyLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {

        val textView = AppCompatTextView(context)
        addView(textView)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        textView.apply {
            text = context.resources.getString(R.string.widget_loading_empty)
            setTextColor(ContextCompat.getColor(context, R.color.textGray))
            textSize = 15f
            gravity = Gravity.CENTER
            val lp = layoutParams
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = dp2px(100)
            if (lp is LayoutParams) {
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