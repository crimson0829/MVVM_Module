package com.crimson.mvvm.binding

import androidx.databinding.BindingAdapter
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener


/**
 * bind smart refresh
 * sm_bindRefresh：绑定下拉刷新监听
 * sm_bindLoadMore：绑定上拉加载监听
 */
@BindingAdapter("app:sm_bindRefresh", "app:sm_bindLoadMore", requireAll = false)
fun SmartRefreshLayout.bindRefresh(
    refreshConsumer: BindConsumer<RefreshLayout>?,
    loadMoreConsumer: BindConsumer<RefreshLayout>?
) {

    setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {

        override fun onRefresh(refreshLayout: RefreshLayout) {

            refreshConsumer?.apply {
                accept(refreshLayout)
            }

        }

        override fun onLoadMore(refreshLayout: RefreshLayout) {
            loadMoreConsumer?.apply {
                accept(refreshLayout)
            }
        }
    })

}