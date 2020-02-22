package com.crimson.mvvm.module

import android.os.Handler
import com.crimson.mvvm.livedata.SingleLiveData
import com.crimson.mvvm.net.NetworkClient
import com.crimson.mvvm.net.RemoteService
import com.crimson.mvvm.rx.bus.RxBus
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * @author crimson
 * @date   2019-12-24
 * koin module
 */

/**
 * local module
 */
val localModule = module {

    //rxbus
    single {
        RxBus.get()
    }

    //live data
    factory {
        SingleLiveData<Any?>()
    }

    //handler
    factory { Handler() }

    //gson
    factory { Gson() }

}

/**
 * remote module
 */
val remoteModule = module {

    single {
        NetworkClient.get(androidContext())
    }

    single {
        get<NetworkClient>().obtainRetrofit()
    }

    single {
        get<NetworkClient>().obtainOkHttp()
    }

    single {
        get<Retrofit>().create(RemoteService::class.java)
    }


}

/**
 * 当需要构建你的module的时候，就会在这个容器里进行检索
 */
val appModule = arrayListOf(
    localModule,
    remoteModule
)

/**
 * 注入新创建的module
 */
fun injectKoinModules(vararg module: Module){
    module.forEach {
        appModule.add(it)
    }
}
