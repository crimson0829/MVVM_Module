package com.crimson.mvvm.rx.retry

import com.crimson.mvvm.ext.logw
import io.reactivex.Flowable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

/**
 * @author crimson
 * @date 2018/7/19
 * rx 重试函数
 */
class RxRetryDelayFunction(private val maxRetries: Int, private val retryDelaySecond: Int) :
    Function<Flowable<out Throwable?>, Flowable<*>> {

    private var retryCount = 0

    override fun apply(observable: Flowable<out Throwable?>): Flowable<*> {
        return observable
            .flatMap { throwable: Throwable? ->
                if (++retryCount <= maxRetries) {
                    // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                    logw(
                        "get error, it will try after " + retryDelaySecond
                                + " millisecond, retry count " + retryCount
                    )
                    Flowable.timer(
                        retryDelaySecond.toLong(),
                        TimeUnit.SECONDS
                    )
                }else{
                    Flowable.error<Any?>(throwable)
                }
            }
    }

}