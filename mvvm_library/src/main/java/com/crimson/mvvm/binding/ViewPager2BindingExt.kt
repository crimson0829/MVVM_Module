package com.crimson.mvvm.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.crimson.mvvm.binding.consumer.BindTiConsumer
import com.crimson.mvvm.binding.transformer.ViewPager2DepthTransformer
import com.crimson.mvvm.binding.transformer.ViewPager2ScaleTransformer
import com.crimson.mvvm.binding.transformer.ViewPager2ZoomOutTransformer
import com.crimson.mvvm.ext.dp2px
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


/**
 * bind viewPager2
 * vp2_adapter：设置 recyclerView adapter
 * vp2_fragmentAdapter：设置 FragmentStateAdapter
 * vp2_orientation：设置方向
 * pageMargin:设置margin值
 * vp2_transformerType：页面的transformerType 1：Scale 2:Depth 3:ZoomOut 目前就这3种
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
    "app:vp2_transformerType",
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
    transformerType: String = "0",
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
    if (pageMargin != 0 || transformerType != "0") {
        val compositePageTransformer = CompositePageTransformer()
        if (pageMargin != 0) {
            compositePageTransformer.addTransformer(MarginPageTransformer(dp2px(pageMargin)))
        }

        when (transformerType) {
            //scale
            "1" -> compositePageTransformer.addTransformer(ViewPager2ScaleTransformer())
            //deptth
            "2" -> compositePageTransformer.addTransformer(ViewPager2DepthTransformer())
            //ZoomOut
            "3" -> compositePageTransformer.addTransformer(ViewPager2ZoomOutTransformer())

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
                    if (it.size > position) {
                        tab.text = it[position]
                    }
                }
            })
            .attach()
    }

}


/**
 * 以下是普通扩展函数
 */
fun ViewPager2.listener(
    onPageScrollStateChanged: (state: Int) -> Unit = { _ -> },
    onPageSelected: (position: Int) -> Unit = { _ -> },
    onPageScrolled: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit = { _, _, _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageSelected(position)
        }
    })
}

fun ViewPager2.onPageScrollStateChanged(onPageScrollStateChanged: (state: Int) -> Unit = { _ -> }) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onPageScrollStateChanged(state)
        }
    })
}

fun ViewPager2.onPageSelected(
    onPageSelected: (position: Int) -> Unit = { _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageSelected(position)
        }
    })
}

fun ViewPager2.onPageScrolled(
    onPageSelected: (position: Int) -> Unit = { _ -> }
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageSelected(position)
        }
    })
}


fun ViewPager2.back(animate: Boolean = true) {
    setCurrentItem(currentItem - 1, animate)
}

fun ViewPager2.forward(animate: Boolean = true) {
    setCurrentItem(currentItem + 1, animate)
}

fun ViewPager2.isOnLastPage(): Boolean {
    return currentItem == (adapter?.itemCount ?: 0) - 1
}

fun ViewPager2.isOnFirstPage(): Boolean {
    return currentItem == 0
}

/**
 * Checks if ViewPager can swipe back.
 */
fun ViewPager2.canGoBack() = currentItem > 0

/**
 * Checks if ViewPager can swipe next
 */
fun ViewPager2.canGoNext() = adapter != null && currentItem < adapter!!.itemCount - 1

/**
 * Swipes ViewPager back
 */
fun ViewPager2.goPrevious() {
    if (canGoBack()) currentItem -= 1
}

/**
 * Swipes ViewPager next
 */
fun ViewPager2.goNext() {
    if (canGoNext()) currentItem += 1
}

val ViewPager2.length: Int?
    get() = adapter?.itemCount

val ViewPager2.lastIndex: Int?
    get() = adapter?.itemCount?.minus(1)

val ViewPager2.isLastView: Boolean
    get() = currentItem == length?.minus(1)

fun ViewPager2.next() {
    if (!isLastView) {
        currentItem += 1
    }
}

fun ViewPager2.next(lastCallback: () -> Unit) {
    if (!isLastView) {
        currentItem += 1
    } else {
        lastCallback()
    }
}

fun ViewPager2.nextCircular() {
    if (!isLastView) {
        currentItem += 1
    } else {
        currentItem = 0
    }
}
