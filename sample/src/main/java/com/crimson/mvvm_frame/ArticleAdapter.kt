package com.crimson.mvvm_frame

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.crimson.mvvm.binding.adapter.BaseBindingAdapter
import com.crimson.mvvm_frame.databinding.AdapterItemArticleBinding
import com.crimson.mvvm_frame.model.kdo.ArticleEntity

/**
 * @author crimson
 * @date   2019-12-30
 * a adapter sample
 */
class ArticleAdapter : BaseBindingAdapter<ArticleEntity, AdapterItemArticleBinding>
    (R.layout.adapter_item_article) {

    override fun convert(holder: BaseViewHolder, item: ArticleEntity?) {

        getDataBinding(holder)?.model=item

    }



}