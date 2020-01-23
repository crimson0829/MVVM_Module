package com.crimson.mvvm.rx

import io.reactivex.Completable
import io.reactivex.CompletableEmitter



fun completable(block: (CompletableEmitter) -> Unit): Completable = Completable.create(block)

fun deferredCompletable(block: () -> Completable): Completable = Completable.defer(block)

fun completableOf(action: () -> Unit): Completable = Completable.fromAction(action)

fun Throwable.toCompletable(): Completable = Completable.error(this)

fun (() -> Throwable).toCompletable(): Completable = Completable.error(this)