package com.crimson.mvvm.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.MainThreadDisposable


/**
 * LiveData转化RxJava
 */
fun <T> LiveData<T>.toFlowable(): Flowable<T> =
    Flowable.create({ emitter ->
        val observer = Observer<T> {
            it?.let(emitter::onNext)
        }

        observeForever(observer)

        emitter.setCancellable {
            object : MainThreadDisposable() {

                override fun onDispose() = removeObserver(observer)
            }
        }
    }, BackpressureStrategy.LATEST)


fun <T> LiveData<T>.toObservable(): Observable<T> = Observable.create { emitter ->
    val observer = Observer<T> {
        it?.let(emitter::onNext)
    }
    observeForever(observer)

    emitter.setCancellable {
        object : MainThreadDisposable() {

            override fun onDispose() = removeObserver(observer)
        }
    }

}
