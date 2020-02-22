package com.crimson.mvvm.binding.adapter

import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *
 * recyclerView adapter dataBinding
 *
 */
open class BaseBindingAdapter<T, B : ViewDataBinding>
@JvmOverloads constructor(@LayoutRes private val layoutResId: Int,
                          data: MutableList<T?>? = null)
    : BaseQuickAdapter<T?,BaseViewHolder>(layoutResId, data) {


    override fun onItemViewHolderCreated(
        viewHolder: BaseViewHolder,
        viewType: Int
    ) { // 绑定 view
        DataBindingUtil.bind<B>(viewHolder.itemView)
    }

    override fun convert(helper: BaseViewHolder, item: T?) {
        if (item == null) {
            return
        }
        // 获取 Binding
        val binding = helper.getBinding<B>()
        binding?.executePendingBindings()
    }


}

