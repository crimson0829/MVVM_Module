package com.crimson.mvvm_frame.app

import com.crimson.mvvm.net.RetrofitApi
import com.crimson.mvvm_frame.ArticleAdapter
import com.crimson.mvvm_frame.AuthorViewModel
import com.crimson.mvvm_frame.TabViewModel
import com.crimson.mvvm_frame.model.AndroidService
import com.crimson.mvvm_frame.model.AuthorModel
import org.koin.dsl.module

/**
 * @author crimson
 * @date   2020-12-22
 * you can build any object in module which you want to inject
 * and add the module when application onCreate
 */

val viewModelModule = module {

    factory { TabViewModel() }
    factory { (id: Int) -> AuthorViewModel(id) }

}

val modelModule = module {

    single {
        AuthorModel()
    }

}

val adapterModule = module {

    factory {
        ArticleAdapter()
    }
}

val dataModule = module {

    single {
        get<RetrofitApi>()
            .obtainRetrofit()
            ?.create(AndroidService::class.java)
    }
}