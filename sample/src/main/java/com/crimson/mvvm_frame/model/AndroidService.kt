package com.crimson.mvvm_frame.model

import com.crimson.mvvm.net.RemoteService
import com.crimson.mvvm_frame.model.kdo.AuthorListEntity
import com.crimson.mvvm_frame.model.kdo.TabListEntity
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author crimson
 * @date   2019-12-22
 */
interface AndroidService : RemoteService {

    companion object {
        const val BASE_URL = "https://www.wanandroid.com/"
    }


    //协程
    @GET("wxarticle/chapters/json")
    suspend fun getTab(): TabListEntity

    //rxjava
    @GET("wxarticle/list/{id}/{page}/json")
    fun getArticles(
        @Path("id") id: Int,
        @Path("page") page: Int

    ): Flowable<AuthorListEntity>
}