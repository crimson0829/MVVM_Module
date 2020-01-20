package com.crimson.mvvm.rx.sp

import com.crimson.mvvm.rx.subOnIOThread
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

/**
 * @author crimson
 * @date   2020-01-19
 * String SP 扩展
 * 尽量少用SP 存储
 */

/**
 * 根据string key 获取对应value
 */
fun String.getBooleanSP(default: Boolean = false): Flowable<Boolean>? {
    SPreferences.get()?.let { sp ->
        return Flowable.create({
            it.onNext(sp.getBoolean(this, default))
            it.onComplete()
        }, BackpressureStrategy.BUFFER)

    }

    return null
}

fun String.getIntSP(defaultValue: Int = 0): Flowable<Int>? {
    SPreferences.get()?.let { sp ->
        return Flowable.create({
            it.onNext(sp.getInt(this, defaultValue))
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }
    return null
}

fun String.getFloatSP(defaultValue: Float = 0f): Flowable<Float>? {
    SPreferences.get()?.let { sp ->
        return Flowable.create({
            it.onNext(sp.getFloat(this, defaultValue))
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }

    return null
}

fun String.getLongSP(defaultValue: Long = 0L): Flowable<Long>? {
    SPreferences.get()?.let { sp ->
        return Flowable.create({
            it.onNext(sp.getLong(this, defaultValue))
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }
    return null
}

fun String.getStringSP(defaultValue: String = ""): Flowable<String>? {
    SPreferences.get()?.let { sp ->
        return Flowable.create({
            sp.getString(this, defaultValue)?.let { it1 -> it.onNext(it1) }
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }
    return null
}

fun String.getStringSetSP(defaultValue: Set<String> = mutableSetOf()): Flowable<Set<String>>? {
    SPreferences.get()?.let { sp ->
        return Flowable.create({
            sp.getStringSet(this, defaultValue)?.let { it1 -> it.onNext(it1) }
            it.onComplete()
        }, BackpressureStrategy.BUFFER)
    }
    return null
}

/**
 * 根据string key 存储对应value
 */
fun String.putStringSP(value: String): Flowable<String>? {
    SPreferences.get()?.let { sp ->
        return Flowable.just(value)
            .doOnNext {
                sp.edit()?.putString(this, value)?.apply()
            }
            .subOnIOThread()
    }
    return null
}

fun String.putInt(value: Int): Flowable<Int>? {
    SPreferences.get()?.let { sp ->
        return Flowable.just(value)
            .doOnNext {
                sp.edit()?.putInt(this, value)?.apply()
            }
            .subOnIOThread()
    }
    return null
}

fun String.putLong(value: Long): Flowable<Long>? {
    SPreferences.get()?.let { sp ->
        return Flowable.just(value)
            .doOnNext {
                sp.edit()?.putLong(this, value)?.apply()
            }
            .subOnIOThread()
    }
    return null
}

fun String.putBoolean(value: Boolean): Flowable<Boolean>? {
    SPreferences.get()?.let { sp ->
        return Flowable.just(value)
            .doOnNext {
                sp.edit()?.putBoolean(this, value)?.apply()
            }
            .subOnIOThread()
    }
    return null
}

fun String.putFloat(value: Float): Flowable<Float>? {
    SPreferences.get()?.let { sp ->
        return Flowable.just(value)
            .doOnNext {
                sp.edit()?.putFloat(this, value)?.apply()
            }
            .subOnIOThread()
    }
    return null
}

fun String.putStringSet(value: Set<String>): Flowable<Set<String>>? {
    SPreferences.get()?.let { sp ->
        return Flowable.just(value)
            .doOnNext {
                sp.edit()?.putStringSet(this, value)?.apply()
            }
            .subOnIOThread()
    }
    return null
}


