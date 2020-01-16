package com.crimson.mvvm.ext

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast


/**
 * app toast
 */
object ToastKt {

    private var toast: Toast? = null

    /**
     * 显示 Toast
     * @param message 提示信息
     * @param duration 显示时间长短
     */
    @SuppressLint("ShowToast")
    fun show(
        message: MESSAGE?,
        duration: Int = Toast.LENGTH_SHORT,
        gravity: Int = Gravity.BOTTOM,
        yOffset: Int = dp2px(80)
    ) {

        if (toast == null) {
            toast = Toast(appContext())
            toast?.setGravity(gravity, 0, yOffset)
            toast?.duration = duration
        }

        if (toast?.view !is FrameLayout) {
            toast?.view = createTextToastView(message)
        } else {
            (((toast?.view as? FrameLayout)?.getChildAt(0)) as? TextView)?.text = message
        }

        toast?.show()

    }

    /**
     * 创建自定义 Toast View
     *
     * @param message 文本消息
     * @return View
     */
    private fun createTextToastView(message: MESSAGE?): View? {

        // 画圆角矩形背景
        val rc = dp2px(6).toFloat()
        val shape =
            RoundRectShape(floatArrayOf(rc, rc, rc, rc, rc, rc, rc, rc), null, null)
        val drawable = ShapeDrawable(shape)
//        drawable.paint.color = Color.argb(225, 25, 240, 240)
        drawable.paint.color = Color.WHITE
        drawable.paint.style = Paint.Style.FILL
        drawable.paint.isAntiAlias = true
        drawable.paint.flags = Paint.ANTI_ALIAS_FLAG
        // 创建View
        val layout = FrameLayout(appContext()!!)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.layoutParams = layoutParams
        layout.setPadding(
            dp2px(16),
            dp2px(12),
            dp2px(16),
            dp2px(12)
        )
        layout.background = drawable
        val textView = TextView(appContext())
        textView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        textView.textSize = 15f
        textView.text = message
        textView.setLineSpacing(dp2px(4).toFloat(), 1f)
        textView.setTextColor(Color.BLACK)
        layout.addView(textView)
        return layout
    }

}

/**
 * toast show
 */
fun toast(
    message: MESSAGE?, duration: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.BOTTOM,
    yOffset: Int = dp2px(80)
) = ToastKt.show(message, duration, gravity, yOffset)

