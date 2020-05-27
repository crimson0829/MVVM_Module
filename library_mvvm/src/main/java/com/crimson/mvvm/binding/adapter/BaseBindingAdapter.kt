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
 * 由于 RecyclerViewBindingExt 中adapter ViewHolder 不能是BaseDataBindingHolder
 * (ERROR: Cannot find a setter for <androidx.recyclerview.widget.RecyclerView app:rv_adapter> that accepts parameter type 'com.crimson.mvvm_frame.ArticleAdapter');
 * 所以为了绑定DataBinding 只能在这里做处理获取 DataBinding
 *
 */

abstract class BaseBindingAdapter<T, B : ViewDataBinding>
@JvmOverloads constructor(
    @LayoutRes private val layoutResId: Int,
    data: MutableList<T?>? = null
) : BaseQuickAdapter<T?, BaseViewHolder>(layoutResId, data) {


    override fun onItemViewHolderCreated(
        holder: BaseViewHolder,
        viewType: Int
    ) {
        // 绑定 view
        DataBindingUtil.bind<B>(holder.itemView)
    }

    /**
     * 获取ViewBinding
     */
    fun getDataBinding(holder: BaseViewHolder) = DataBindingUtil.getBinding<B>(holder.itemView)


}

