package com.crimson.mvvm.database



sealed class DBResult<out T> {

    data class Success<T>(val value: T) : DBResult<T>()
    object Querying : DBResult<Nothing>()
    object EmptyDB : DBResult<Nothing>()
    data class DBError(val throwable: Throwable) : DBResult<Nothing>()

}

