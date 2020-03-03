package com.crimson.mvvm.binding

import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.crimson.mvvm.binding.consumer.BindConsumer
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.SlidingTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener

/**
 * @author crimson
 * @date   2020-02-14
 * FlycoTabLayout 扩展
 */

/**
 * 给tabLayout设置标题
 */
@BindingAdapter("app:tabTitles")
fun CommonTabLayout.setTitles(titles: List<String>?) {
    val entitys = arrayListOf<CustomTabEntity>()
    titles?.forEach {
        entitys.add(TabEntity(it))
    }
    if (entitys.isEmpty()) {
        return
    }
    setTabData(entitys)
}

@BindingAdapter("app:tabSelectChanged")
fun CommonTabLayout.setSelectListener(consumer: BindConsumer<Int>?) {
    consumer?.apply {
        setOnTabSelectListener(object : OnTabSelectListener {

            override fun onTabSelect(position: Int) {
                consumer.accept(position)
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }

}

@BindingAdapter("app:stabSelectChanged")
fun SlidingTabLayout.setSelectListener(consumer: BindConsumer<Int>?){
    consumer?.apply {
        setOnTabSelectListener(object : OnTabSelectListener {

            override fun onTabSelect(position: Int) {
                consumer.accept(position)
            }

            override fun onTabReselect(position: Int) {
            }
        })
    }
}

/**
 * 设置当前选定tab
 */
@BindingAdapter("app:tabIndex")
fun CommonTabLayout.currentTab(tabIndex: Int = 0) {
    currentTab = tabIndex
}

@BindingAdapter("app:stabIndex")
fun SlidingTabLayout.currentTab(tabIndex: Int = 0) {
    currentTab = tabIndex
}



class TabEntity(
    val title: String,
    val unselectedIcon: Int = 0,
    val selectedIcon: Int = 0
) :
    CustomTabEntity {
    override fun getTabUnselectedIcon(): Int {
        return unselectedIcon
    }

    override fun getTabSelectedIcon(): Int {
        return selectedIcon
    }

    override fun getTabTitle(): String {
        return title
    }
}


/**
 *  绑定vp1
 */
fun SlidingTabLayout.setDataWithFragment(
    viewPager: ViewPager,
    titles: Array<String>,
    fa: FragmentActivity,
    fragments: ArrayList<Fragment>
) {

    setViewPager(viewPager, titles, fa, fragments)

}