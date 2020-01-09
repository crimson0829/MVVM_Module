package com.crimson.mvvm.rx

import io.reactivex.Flowable
import io.reactivex.Observable
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

fun <T> Observable<T>.subOnIOThread(): Observable<T> =
    this.subscribeOn(Schedulers.io())

fun <T> Flowable<T>.subOnIOThread(): Flowable<T> =
    this.subscribeOn(Schedulers.io())

fun <T> Observable<T>.observeOnMainThread(): Observable<T> =
    this.observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.observeOnMainThread(): Flowable<T> =
    this.observeOn(AndroidSchedulers.mainThread())



