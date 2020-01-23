package com.crimson.mvvm_frame.model

import com.crimson.mvvm.base.BaseModel
import com.crimson.mvvm.rx.applyThread
import org.koin.core.inject

/**
 * @author crimson
 * @date   2019-12-22
 * model层
 * 获取网络或本地数据，交给viewModel处理，也可自己处理逻辑
 */
class AuthorModel : BaseModel() {

    val androidService by inject<AndroidService>()


    /**
     * use 协程
     */
    suspend fun getData() = androidService.getTab()

    /**
     * get data use flowable
     */
    fun getAuthorListData(id: Int, page: Int) = androidService.getArticles(id, page).applyThread()

}


