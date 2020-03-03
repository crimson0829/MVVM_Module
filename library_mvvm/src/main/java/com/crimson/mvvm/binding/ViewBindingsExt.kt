@file:Suppress("CheckResult")

package com.crimson.mvvm.binding


import android.view.View
import android.view.ViewTreeObserver
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.crimson.mvvm.rx.observeOnMainThread
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
 * clickDuration：点击事件间隔
 */
@BindingAdapter("app:bindClick", "app:clickDuration", requireAll = false)
fun View.bindClick(clickConsumer: BindConsumer<Unit?>?, duration: Long = 500) {
    clickConsumer?.apply {
        (context as? LifecycleOwner)?.let { owner ->
            clicks()
                .throttleLast(duration, TimeUnit.MILLISECONDS)
                .bindUntilEvent(owner, Lifecycle.Event.ON_DESTROY)
                .observeOnMainThread()
                .subscribe {
                    accept(it)
                }
        }
    }

}

/**
 * bind long click
 * bindLongClick：绑定长按点击
 */
@BindingAdapter("app:bindLongClick")
fun View.bindLongClick(clickConsumer: BindConsumer<Unit?>?) {
    clickConsumer?.apply {
        (context as? LifecycleOwner)?.let { owner ->
            longClicks()
                .observeOnMainThread()
                .bindUntilEvent(owner,Lifecycle.Event.ON_DESTROY)
                .subscribe {
                    accept(it)
                }

        }

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










