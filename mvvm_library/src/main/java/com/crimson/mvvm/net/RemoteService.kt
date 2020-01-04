package com.crimson.mvvm.net

import com.crimson.mvvm.net.poko.BaseEntity
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @author crimson
 * @date   2019-12-23
 */
interface RemoteService {

    //协程
    @GET
    suspend fun <T> getDataWithCoroutine(
       @Url url:String
    ): BaseEntity<T>

    //rxjava
    @GET
    fun <T> getDataWithRx(
        @Url url:String
    ): Flowable<BaseEntity<T>>


}