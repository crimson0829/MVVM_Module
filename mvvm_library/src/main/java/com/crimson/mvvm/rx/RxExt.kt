package com.crimson.mvvm.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author crimson
 * @date   2020-12-20
 * rx ext
 */
fun <T> Observable<T>.applyThread(): Observable<T> =
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.applyThread(): Flowable<T> =
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

/**
 * rx 线程调度
 */
fun rxIoThread(): Scheduler = Schedulers.io()

fun rxMainThread(): Scheduler = AndroidSchedulers.mainThread()

