package com.crimson.mvvm.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.crimson.mvvm.binding.consumer.BindBiConsumer
import com.crimson.mvvm.binding.consumer.BindTiConsumer
import com.crimson.mvvm.binding.recyclerview.LayoutManagers
import com.crimson.mvvm.binding.recyclerview.LineManagers


/**
 * bind recycler view
 * 适配器viewHolder 必须继承 BaseViewHolder
 * rv_adapter：设置适配器
 * rv_layoutManager：设置布局管理器
 * rv_lineManager：设置Item间隔管理器
 * rv_bindScrollStateChanged：滑动状态监听
 * rv_bindScrolled：滑动监听
 */
@BindingAdapter(
    "app:rv_adapter",
    "app:rv_layoutManager",
    "app:rv_lineManager",
    "app:rv_bindScrollStateChanged",
    "app:rv_bindScrolled",
    requireAll = false
)
fun  RecyclerView.bindAdapter(
    adapter: RecyclerView.Adapter<BaseViewHolder>?,
    layoutManager: LayoutManagers.LayoutManagerFactory?,
    lineManager: LineManagers.LineManagerFactory? = null,
    scrollStateChangedConsumer: BindBiConsumer<RecyclerView, Int>? = null,
    scrolledConsumer: BindTiConsumer<RecyclerView, Int, Int>? = null
) {

    adapter?.let {
        this.adapter = adapter
    }
    layoutManager?.let {
        this.layoutManager = layoutManager.create(this)
    }
    lineManager?.let {
        this.addItemDecoration(lineManager.create(this))
    }

    if (scrollStateChangedConsumer != null || scrolledConsumer != null) {

        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollStateChangedConsumer?.apply {
                    accept(recyclerView, newState)
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrolledConsumer?.apply {
                    accept(recyclerView, dx, dy)
                }
            }
        })
    }

}