package com.crimson.mvvm_frame.model

import com.crimson.mvvm.base.BaseModel
import com.crimson.mvvm.rx.applyThread
import com.crimson.mvvm_frame.model.kdo.AuthorListEntity
import com.crimson.mvvm_frame.model.kdo.TabListEntity
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.inject

/**
 * @author crimson
 * @date   2019-12-22
 * 获取网络或本地数据，交给viewModel处理，也可自己处理逻辑
 */
class AuthorModel : BaseModel() {

    val androidService by inject<AndroidService>()

    /**
     * get data use coroutine
     */
    suspend fun getTabData(): TabListEntity {
        return callRemoteTabData { androidService.getTab() }
    }

    private suspend fun callRemoteTabData(call: suspend () -> TabListEntity): TabListEntity {
        return withContext(Dispatchers.IO) { call.invoke() }
    }

    /**
     * get data use flowable
     */
    fun getAuthorListData(id: Int, page: Int): Flowable<AuthorListEntity> {
        return androidService.getArticles(id, page)
            .applyThread()

    }

}
