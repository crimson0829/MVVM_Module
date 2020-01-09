package com.crimson.mvvm.binding

import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer

/**
 * @author crimson
 * @date   2020-01-08
 * bind consumer function
 */

interface BindConsumer<T> : Consumer<T>

interface BindBiConsumer<T1, T2> : BiConsumer<T1, T2>

interface BindTiConsumer<T1, T2, T3> : TiConsumer<T1, T2, T3>

interface TiConsumer<T1, T2, T3> {

    @Throws(Exception::class)
    fun accept(t1: T1, t2: T2, t3: T3)

}

/**
 * bind consumer
 */
inline fun <reified T> bindConsumer(crossinline call: T.() -> Unit): BindConsumer<T> {
    return object : BindConsumer<T> {
        override fun accept(t: T) {
            call(t)
        }
    }

}

/**
 * bind biConsumer
 *
 */
inline fun <reified T1, reified T2> bindBiConsumer(crossinline call: (T1, T2) -> Unit): BindBiConsumer<T1, T2> {
    return object : BindBiConsumer<T1, T2> {
        override fun accept(t1: T1, t2: T2) {
            call(t1, t2)
        }
    }

}

/**
 * bind tiConsumer
 *
 */
inline fun <reified T1, reified T2, reified T3> bindTiConsumer(crossinline call: (T1, T2, T3) -> Unit):
        BindTiConsumer<T1, T2, T3> {
    return object : BindTiConsumer<T1, T2, T3> {
        override fun accept(t1: T1, t2: T2, t3: T3) {
            call(t1, t2, t3)
        }
    }

}

