@file:Suppress("CheckResult")

package com.crimson.mvvm.binding


import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.crimson.mvvm.binding.recyclerview.LayoutManagers
import com.crimson.mvvm.binding.recyclerview.LineManagers
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.concurrent.TimeUnit


/**
 * @author crimson
 * @date   2019-12-24
 * View Bind
 * more bind function will add
 */

interface BindConsumer<T> : Consumer<T>

/**
 * bind consumer
 */
fun <T> bindConsumer(call: BindConsumer<T>.() -> Unit): BindConsumer<T> {
    return object : BindConsumer<T> {
        override fun accept(t: T) {
            call()
        }
    }

}

/**
 * bind image
 */
@BindingAdapter(
    "app:imageUrl",
    "app:skipMemoryCache",
    "app:diskMemoryCache",
    "app:imagePlaceholder",
    "app:imageError",
    "app:imageStyle",
    requireAll = false
)
fun ImageView.bindImage(
    imageUrl: String?,
    skipMemoryCache: Boolean = false,
    diskMemoryCache: String? = "1",
    @DrawableRes imagePlaceholder: Int = 0,
    @DrawableRes imageError: Int = 0,
    imageStyle: String? = "1"
) {

    val builder = Glide.with(context)
        .load(imageUrl)
        .skipMemoryCache(skipMemoryCache)
        .placeholder(imagePlaceholder)
        .error(imageError)

    when (diskMemoryCache) {

        "1" -> builder.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        "2" -> builder.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        "3" -> builder.diskCacheStrategy(DiskCacheStrategy.DATA)
        "4" -> builder.diskCacheStrategy(DiskCacheStrategy.NONE)
        "5" -> builder.diskCacheStrategy(DiskCacheStrategy.ALL)
        else -> builder.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    }

    when (imageStyle) {
        "1" -> builder.centerCrop()
        "2" -> builder.circleCrop()
        "3" -> builder.transform(BlurTransformation(25, 5))
        else -> builder.centerCrop()
    }

    builder.into(this)

}

/**
 * bind recycler view
 */
@BindingAdapter("app:adapter", "app:layoutManager", "app:lineManager", requireAll = false)
fun RecyclerView.bindAdapter(
    adapter: RecyclerView.Adapter<BaseViewHolder>?,
    layoutManager: LayoutManagers.LayoutManagerFactory?,
    lineManager: LineManagers.LineManagerFactory?
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


}

/**
 * bind smart refresh
 */
@BindingAdapter("app:bindRefresh", "app:bindLoadMore", requireAll = false)
fun SmartRefreshLayout.bindRefresh(
    refreshConsumer: BindConsumer<RefreshLayout>?,
    loadMoreConsumer: BindConsumer<RefreshLayout>?
) {

    setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {

        override fun onRefresh(refreshLayout: RefreshLayout) {

            refreshConsumer?.apply {
                refreshConsumer.accept(refreshLayout)
            }

        }

        override fun onLoadMore(refreshLayout: RefreshLayout) {
            loadMoreConsumer?.apply {
                loadMoreConsumer.accept(refreshLayout)
            }
        }
    })

}


/**
 * bind click
 */
@BindingAdapter("app:bindClick", "app:clickDuration", requireAll = false)
fun View.bindClick(clickConsumer: BindConsumer<Unit?>?, duration: Long = 500) {
    clickConsumer?.apply {
        clicks()
            .throttleLast(duration, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                clickConsumer.accept(it)
            }
    }


}

/**
 * bind long click
 */
@BindingAdapter("app:bindLongClick")
fun View.bindLongClick(clickConsumer: BindConsumer<Unit?>?) {
    clickConsumer?.apply {
        longClicks()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                clickConsumer.accept(it)
            }
    }

}


/**
 * bind text changes
 */
@BindingAdapter("app:textChanges")
fun EditText.textChanges(changesConsumer: BindConsumer<CharSequence>?) {
    changesConsumer?.apply {
        textChanges()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                changesConsumer.accept(it)
            }
    }

}









