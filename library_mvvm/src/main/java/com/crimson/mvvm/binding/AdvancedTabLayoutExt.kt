package com.crimson.mvvm.binding

import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.crimson.library.tab.AdvancedTabLayout
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.crimson.mvvm.binding.consumer.BindTiConsumer

/**
 * @author crimson
 * @date   2020/3/23
 * AdvancedTabLayout 扩展
 */
@BindingAdapter("app:tabTitles")
fun AdvancedTabLayout.bindTitles(titles: List<String>?) {
    setTabData(titles)
}

@BindingAdapter("app:tabSelectChanged", "app:tabReselectChanged",requireAll = false)
fun AdvancedTabLayout.bindSelectListener(
    selectConsumer: BindConsumer<Int>?,
    reselectConsumer: BindConsumer<Int>?
) {
    setOnTabSelectListener({
        selectConsumer?.accept(it)
    }, {
        reselectConsumer?.accept(it)
    })

}

@BindingAdapter("app:tabVP2SelectChanged", "app:tabVP2ScrollChanged","app:tabVP2ScrollStateChanged",requireAll = false)
fun AdvancedTabLayout.bindViewPager2ScrollListener(
    selectConsumer: BindConsumer<Int>?,
    scrollConsumer: BindTiConsumer<Int, Float, Int>?,
    scrollStateConsumer: BindConsumer<Int>?
) {
    setViewPage2ScrollListener({
        selectConsumer?.accept(it)
    }, { position, positionOffset, positionOffsetPixels ->
        scrollConsumer?.accept(position, positionOffset, positionOffsetPixels)
    }, {
        scrollStateConsumer?.accept(it)
    })
}

/**
 * 设置当前选定tab
 */
@BindingAdapter("app:tabIndex", "app:tabVP2SmoothScroll",requireAll = false)
fun AdvancedTabLayout.currentTab(tabIndex: Int = 0, smoothScroll: Boolean = true) {
    setCurrentTab(tabIndex, smoothScroll)
}


fun AdvancedTabLayout.bindViewPager2(
    viewPager: ViewPager2,
    fa: FragmentActivity?,
    fragments: ArrayList<Fragment>?,
    titles: List<String>?
) {

    setViewPager2(viewPager, fa, fragments, titles)

}