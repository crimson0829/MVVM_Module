package com.crimson.mvvm.rx.retry

import android.content.Context
import com.crimson.mvvm.rx.retry.BroadcastFlowable.Companion.fromConnectivityManager
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.functions.Function
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author crimson
 * @date 2019/7/19
 * rx 重试函数
 */
class RxRetryFunction(
    context: Context,
    private val startTimeOut: Int,
    private val maxTimeout: Int,
    private val timeUnit: TimeUnit
) : Function<Flowable<out Throwable?>, Flowable<*>> {

    private val isConnected: Flowable<Boolean>
    private var timeout: Int

    init {
        timeout = startTimeOut
        isConnected = getConnectedObservable(context)
    }

    private fun attachIncrementalTimeout(): FlowableTransformer<Boolean?, Boolean?> {
        return FlowableTransformer { observable: Flowable<Boolean?> ->
            observable.timeout(timeout.toLong(), timeUnit)
                .doOnError { throwable: Throwable? ->
                    if (throwable is TimeoutException) {
                        timeout = if (timeout > maxTimeout) maxTimeout else timeout + startTimeOut
                    }
                }
        }
    }

    private fun getConnectedObservable(context: Context): Flowable<Boolean> {
        return fromConnectivityManager(context)
            .distinctUntilChanged()
            .filter { isConnected: Boolean? -> isConnected!! }
    }

    override fun apply(observable: Flowable<out Throwable?>): Flowable<*> {
        return observable.flatMap { throwable: Throwable? ->
            if (throwable is UnknownHostException) {
                isConnected
            } else {
                Flowable.error<Boolean>(throwable)
            }
        }
            .compose(attachIncrementalTimeout())
    }


}