@file:Suppress("CheckResult")

package com.crimson.mvvm.binding


import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.crimson.mvvm.binding.recyclerview.LayoutManagers
import com.crimson.mvvm.binding.recyclerview.LineManagers
import com.crimson.mvvm.binding.transformer.ViewPager2ScaleTransformer
import com.crimson.mvvm.ext.dp2px
import com.crimson.mvvm.rx.observeOnMainThread
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.util.concurrent.TimeUnit


/**
 * @author crimson
 * @date   2019-12-24
 * View Bind
 * View扩展函数绑定DataBinding，方便xml或者代码调用
 * more bind function will add
 */

/**
 * bind image with glide
 * imageUrl：图片链接
 * imageStyle：加载方式
 * imageRoundShape：设置图片圆角大小
 * image_skipMemoryCache：是否忽略内存缓存
 * image_diskMemoryCache：本地缓存策略
 * imagePlaceholder：欲加载显示图片
 * imageError：加载错误显示图片
 */
@BindingAdapter(
    "app:imageUrl",
    "app:imageStyle",
    "app:imageRoundShape",
    "app:image_skipMemoryCache",
    "app:image_diskMemoryCache",
    "app:imagePlaceholder",
    "app:imageError",
    requireAll = false
)
fun ImageView.bindImage(
    imageUrl: String?,
    imageStyle: String? = "1",
    imageRoundShape: Int? = 0,
    skipMemoryCache: Boolean = false,
    diskMemoryCache: String? = "1",
    @DrawableRes imagePlaceholder: Int = 0,
    @DrawableRes imageError: Int = 0
) {

    val builder = Glide.with(context)
        .load(imageUrl)
        .skipMemoryCache(skipMemoryCache)
        .placeholder(imagePlaceholder)
        .centerCrop()
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
        //默认
        "1" -> builder.centerCrop()
        //round shape，设置imageStyle=2 再设置imageRoundShape>0才有效果
        "2" -> {
            if (imageRoundShape != 0) {
                builder.transform(RoundedCornersTransformation(dp2px(imageRoundShape ?: 0), 0))
            } else {
                builder.centerCrop()
            }
        }
        //circle
        "3" -> builder.circleCrop()
        //blur 高斯模糊
        "4" -> builder.transform(BlurTransformation(25, 5))
        "5" -> builder.centerInside()
        "6" -> builder.fitCenter()
        else -> builder.centerCrop()

    }

    builder.into(this)

}

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
fun RecyclerView.bindAdapter(
    adapter: RecyclerView.Adapter<BaseViewHolder>?,
    layoutManager: LayoutManagers.LayoutManagerFactory?,
    lineManager: LineManagers.LineManagerFactory? = null,
    scrollStateChangedConsumer: BindConsumer<Int>? = null,
    scrolledConsumer: BindBiConsumer<Int, Int>? = null
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
                    accept(newState)
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrolledConsumer?.apply {
                    accept(dx, dy)
                }
            }
        })
    }

}

/**
 * bind viewPager2
 * vp2_adapter：设置 recyclerView adapter
 * vp2_fragmentAdapter：设置 FragmentStateAdapter
 * vp2_orientation：设置方向
 * pageMargin:设置margin值
 * vp2_needScaleTransformer：是否需要页面缩放
 * vp2_multiPagePadding：蛇追多页显示间距
 * vp2_bindPageSelected：页面选定监听
 * vp2_bindPageScrollStateChanged：页面滑动状态监听
 * vp2_bindPageScrolled：页面滑动监听
 *
 */
@BindingAdapter(
    "app:vp2_adapter",
    "app:vp2_fragmentAdapter",
    "app:vp2_orientation",
    "app:vp2_pageMargin",
    "app:vp2_needScaleTransformer",
    "app:vp2_multiPagePadding",
    "app:vp2_bindPageSelected",
    "app:vp2_bindPageScrollStateChanged",
    "app:vp2_bindPageScrolled",
    requireAll = false
)
fun ViewPager2.bindAdapter(
    adapter: RecyclerView.Adapter<BaseViewHolder>? = null,
    fragmentAdapter: FragmentStateAdapter? = null,
    orientation: Int = 0,
    pageMargin: Int = 0,
    needScaleTransformer: Boolean = false,
    multiPagePadding: Int = 0,
    pageSelectedConsumer: BindConsumer<Int>? = null,
    pageScrollStateChangedConsumer: BindConsumer<Int>? = null,
    pageScrolledConsumer: BindTiConsumer<Int, Float, Int>? = null

) {

    //viewpager2绑定适配器，两种绑定方式；
    // 1：绑定 recyclerView adapter;
    // 2：绑定 FragmentStateAdapter；
    if (adapter != null) {
        this.adapter = adapter
    } else if (fragmentAdapter != null) {
        this.adapter = fragmentAdapter
    }

    //设置方向
    if (orientation == 0) {
        this.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    } else {
        this.orientation = ViewPager2.ORIENTATION_VERTICAL
    }

    //设置page transformer
    //默认绑定了缩放和margin，如果想更多的效果，请自行设置
    if (pageMargin != 0 || needScaleTransformer) {
        val compositePageTransformer = CompositePageTransformer()
        if (needScaleTransformer) {
            compositePageTransformer.addTransformer(ViewPager2ScaleTransformer())
        }
        if (pageMargin != 0) {
            compositePageTransformer.addTransformer(MarginPageTransformer(dp2px(pageMargin)))
        }
        this.setPageTransformer(compositePageTransformer)
    }

    //设置多页显示
    if (multiPagePadding != 0) {
        val recyclerView = getChildAt(0) as? RecyclerView
        recyclerView?.apply {
            // setting padding on inner RecyclerView puts overscroll effect in the right place
            setPadding(dp2px(multiPagePadding), 0, dp2px(multiPagePadding), 0)
            clipToPadding = false
        }
    }

    //注册page监听事件
    if (pageScrollStateChangedConsumer != null || pageSelectedConsumer != null || pageScrolledConsumer != null) {
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                pageScrollStateChangedConsumer?.apply {
                    accept(state)
                }

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pageSelectedConsumer?.apply {
                    accept(position)
                }

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                pageScrolledConsumer?.apply {
                    accept(position, positionOffset, positionOffsetPixels)
                }
            }

        })

    }


}

/**
 * bind tabLayout
 * vp2_bindTabLayout:绑定tabLayout
 * vp2_tabLayoutTitles:设置tabLayout标题
 */
@BindingAdapter("app:vp2_bindTabLayout", "app:vp2_tabLayoutTitles", requireAll = true)
fun ViewPager2.bindTabLayout(tabLayout: TabLayout?, titles: MutableList<String>?) {
    tabLayout?.let {
        TabLayoutMediator(tabLayout, this,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                titles?.let {
                    //设置 title
                    tab.text = it[position]
                }
            })
            .attach()
    }

}


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


/**
 * bind click
 * bindClick：绑定点击
 * clickDuration：下次点击事件间隔
 */
@BindingAdapter("app:bindClick", "app:clickDuration", requireAll = false)
fun View.bindClick(clickConsumer: BindConsumer<Unit?>?, duration: Long = 500) {
    clickConsumer?.apply {
        clicks()
            .throttleLast(duration, TimeUnit.MILLISECONDS)
            .observeOnMainThread()
            .subscribe {
                accept(it)
            }
    }

}

/**
 * bind long click
 * bindLongClick：绑定长按点击
 */
@BindingAdapter("app:bindLongClick")
fun View.bindLongClick(clickConsumer: BindConsumer<Unit?>?) {
    clickConsumer?.apply {
        longClicks()
            .observeOnMainThread()
            .subscribe {
                accept(it)
            }
    }

}


/**
 * bind text changes
 * textChanges：绑定文字改变监听
 */
@BindingAdapter("app:textChanges")
fun EditText.textChanges(changesConsumer: BindConsumer<CharSequence>?) {
    changesConsumer?.apply {
        textChanges()
            .observeOnMainThread()
            .subscribe {
                accept(it)
            }
    }

}










