package com.crimson.mvvm.base

import com.crimson.mvvm.net.RemoteService
import com.crimson.mvvm.net.poko.BaseEntity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.koin.core.inject


/**
 * @author crimson
 * @date   2019-12-21
 * base model
 */
open class BaseModel : IModel {

    val remoteService by inject<RemoteService>()


    /**
     * call with coroutines
     */
    suspend fun <T> remoteCall(call: suspend () -> BaseEntity<T>): BaseEntity<T> {

        return withContext(IO) { call.invoke() }
    }


}