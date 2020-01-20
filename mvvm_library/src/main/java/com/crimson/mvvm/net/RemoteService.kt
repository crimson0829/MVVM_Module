package com.crimson.mvvm.net

import com.crimson.mvvm.net.poko.BaseEntity
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @author crimson
 * @date   2019-12-23
 * 服务接口，可自定义
 */
interface RemoteService {

    //协程获取数据
    @GET
    suspend fun <T> getData(
       @Url url:String
    ): BaseEntity<T>

    //rxjava获取数据
    @GET
    fun <T> getDataWithRx(
        @Url url:String
    ): Flowable<BaseEntity<T>>


}