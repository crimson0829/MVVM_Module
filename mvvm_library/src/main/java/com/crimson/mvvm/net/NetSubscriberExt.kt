package com.crimson.mvvm.net

import com.crimson.mvvm.ext.loge
import com.crimson.mvvm.ext.toast
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

/**
 * 网络请求统一订阅处理，目前只处理了报错弹出toast的情况
 */

private val onNextStub: (Any) -> Unit = {}

//重写rxkotlin,全局toast异常
private val onErrorStub: (Throwable?) -> Unit = {
    loge(it?.message)
    //全部甩锅网络异常
    toast("网络异常")
}
private val onCompleteStub: () -> Unit = {}


/**
 * Overloaded subscribe function that allows passing named parameters
 */
fun <T : Any> Flowable<T>.subscribeNet(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
): Disposable = subscribe(onNext, onError, onComplete)