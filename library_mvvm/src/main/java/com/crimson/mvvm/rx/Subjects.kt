package com.crimson.mvvm.rx

import io.reactivex.subjects.*


fun <T> AsyncSubject(): AsyncSubject<T> = AsyncSubject.create()

fun <T> BehaviorSubject(): BehaviorSubject<T> = BehaviorSubject.create()

fun <T> BehaviorSubject(default : T): BehaviorSubject<T> = BehaviorSubject.createDefault(default)

fun CompletableSubject(): CompletableSubject = CompletableSubject.create()

fun <T> MaybeSubject(): MaybeSubject<T> = MaybeSubject.create()

fun <T> PublishSubject(): PublishSubject<T> = PublishSubject.create()

fun <T> ReplaySubject(): ReplaySubject<T> = ReplaySubject.create()

fun <T> SingleSubject(): SingleSubject<T> = SingleSubject.create()

fun <T> UnicastSubject(): UnicastSubject<T> = UnicastSubject.create()

