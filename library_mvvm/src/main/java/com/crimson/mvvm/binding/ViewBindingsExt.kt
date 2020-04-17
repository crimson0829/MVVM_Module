@file:Suppress("CheckResult")

package com.crimson.mvvm.binding


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.crimson.mvvm.ext.view.dp2px
import com.crimson.mvvm.rx.observeOnMainThread
import com.crimson.mvvm.utils.AntiShakeUtils
import com.crimson.mvvm.utils.RoomUtils
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindUntilEvent
import java.util.concurrent.TimeUnit


/**
 * @author crimson
 * @date   2019-12-24
 * View Bind
 * View扩展函数绑定DataBinding，方便xml或者代码调用
 * more bind function will be add
 */


/**
 * bind click
 * bindClick：绑定点击
 * clickDuration：下次点击事件间隔
 */
@BindingAdapter("app:bindClick", "app:clickDuration", "app:bindClickError", requireAll = false)
fun View.bindClick(
    clickConsumer: BindConsumer<Unit?>?,
    duration: Long = 500,
    clickErrorConsumer: BindConsumer<Throwable>?=null
) {

    if (RoomUtils.isOppo) {
        //oppo手机如果调用了rxbinding3->throttleLast那么就一直会后台gc，导致app一到后台就有大概率被杀死，需判断
        setOnClickListener {
            if (AntiShakeUtils.isInvalidClick(this, 500)) {
                return@setOnClickListener
            }
            clickConsumer?.accept(null)
        }
    } else {
        (context as? LifecycleOwner)?.let { owner ->

            clicks()
                .throttleLast(duration, TimeUnit.MILLISECONDS)
                .bindUntilEvent(owner, Lifecycle.Event.ON_DESTROY)
                .observeOnMainThread()
                .subscribe({
                    clickConsumer?.accept(it)
                }, {
                    clickErrorConsumer?.accept(it)
                })


        }
    }


}

/**
 * bind long click
 * bindLongClick：绑定长按点击
 */
@BindingAdapter("app:bindLongClick", "app:bindLongClickError", requireAll = false)
fun View.bindLongClick(
    clickConsumer: BindConsumer<Unit?>?,
    clickErrorConsumer: BindConsumer<Throwable>?=null
) {
    (context as? LifecycleOwner)?.let { owner ->
        longClicks()
            .observeOnMainThread()
            .bindUntilEvent(owner, Lifecycle.Event.ON_DESTROY)
            .subscribe(
                {
                    clickConsumer?.accept(it)
                }, {
                    clickErrorConsumer?.accept(it)
                }
            )

    }

}


/**
 * 视图树绑定
 */
@BindingAdapter("app:bindPreDraw")
fun View.bindViewTreeObserver(preDrawConsumer: BindConsumer<Unit>?) {
    preDrawConsumer?.apply {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)
                preDrawConsumer.accept(null)
                return true
            }
        })
    }

}

@BindingAdapter("app:bindGlobalLayout")
fun View.onGlobalLayout(globalLayoutConsumer: BindConsumer<Unit>?) {
    globalLayoutConsumer?.apply {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    globalLayoutConsumer.accept(null)

                }
            }
        })
    }

}

/**
 * 设置背景shape 和stroke
 *
 */
@BindingAdapter(
    "app:bg_bgColor",
    "app:bg_cornerRadius",
    "app:bg_strokeColor",
    "app:bg_strokeWidth",
    requireAll = false
)
fun View.bindBackGroundShape(
    bgColor: Int = Color.BLACK,
    cornerRadius: Int = 0,
    strokeColor: Int = Color.TRANSPARENT,
    strokeWidth: Int = 0
) {

    val bgColorInt = Color.argb(
        Color.alpha(bgColor),
        Color.red(bgColor),
        Color.green(bgColor),
        Color.blue(bgColor)
    )


    val stokeColorInt = Color.argb(
        Color.alpha(strokeColor),
        Color.red(strokeColor),
        Color.green(strokeColor),
        Color.blue(strokeColor)
    )

    val gd = GradientDrawable()
    gd.setColor(bgColorInt)
    gd.cornerRadius = dp2px(cornerRadius)?.toFloat() ?: 0f
    gd.setStroke(dp2px(strokeWidth) ?: 0, stokeColorInt)


    val bg = StateListDrawable()
    bg.addState(intArrayOf(-android.R.attr.state_pressed), gd)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = bg
    } else {
        setBackgroundDrawable(bg)
    }
}










