package com.crimson.mvvm.utils.fps

import android.view.Choreographer
import com.crimson.mvvm.ext.loge

/**
 * @author crimson
 * @date 2020/6/4
 */
class FPSFrameCallback(var lastFrameTimeNanos: Long = System.nanoTime()) :
    Choreographer.FrameCallback {

    private val mFrameIntervalNanos: Long = (1000000000 / 60.0).toLong()

    override fun doFrame(frameTimeNanos: Long) {

        //初始化时间
        if (lastFrameTimeNanos == 0L) {
            lastFrameTimeNanos = frameTimeNanos
        }
        val jitterNanos = frameTimeNanos - lastFrameTimeNanos
        if (jitterNanos >= mFrameIntervalNanos) {
            val skippedFrames = jitterNanos / mFrameIntervalNanos
            //            LogExtKt.loge(  skippedFrames + " frames!  ");
            if (skippedFrames > 30) {
                loge(
                    "Skipped " + skippedFrames + " frames!  "
                            + "The application may be doing too much work on its main thread."
                )
            }
        }
        lastFrameTimeNanos = frameTimeNanos
        //注册下一帧回调
        Choreographer.getInstance().postFrameCallback(this)
    }

}