package com.crimson.mvvm_frame

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.crimson.mvvm.base.BaseActivity
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.adapter.ViewPager2FragmentAdapter
import com.crimson.mvvm.ext.logw
import com.crimson.mvvm.livedata.SingleLiveData
import com.crimson.mvvm.rx.bus.RxCode
import com.crimson.mvvm.rx.bus.RxDisposable
import com.crimson.mvvm_frame.databinding.ActivityTabBinding
import com.crimson.mvvm_frame.model.AuthorModel
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.inject

/**
 * @author crimson
 * @date   2019-12-22
 *  use viewpager2 with tablayout
 *  see: https://developer.android.com/guide/navigation/navigation-swipe-view-2
 */
class TabActivity : BaseActivity<ActivityTabBinding, TabViewModel>() {


    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_tab
    }

    override fun initViewModelId(): Int {
        return BR.viewModel
    }

    override fun initView() {

        vm?.getData()

    }

    override fun initViewObservable() {

        vm?.tabDataCompleteLD?.observe(this, Observer { it ->

            vb?.viewPager?.apply {
                vm?.fragments?.let {
                    //设置viewpager2 adapter
                    adapter = ViewPager2FragmentAdapter(this@TabActivity, it)
                }

                vb?.tabLayout?.let { layout ->
                    //tab绑定viewpager2
                    TabLayoutMediator(layout, this,
                        TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                            it?.let {
                                val s = it[position]
                                logw("title -> $s")
                                tab.text = s
                            }
                        })
                        .attach()
                }


            }

        })


    }
}

/**
 * data from coroutine
 */
class TabViewModel : BaseViewModel() {

    //koin inject
    val model by inject<AuthorModel>()

    //live data
    val tabDataCompleteLD by inject<SingleLiveData<Array<String>>>()

    val fragments = arrayListOf<Fragment>()

    var errorDis:Disposable?=null

    /**
     * run with 协程
     */
    fun getData() =

        launchCoroutine {

            onLoadingViewInjectToRoot()
//            delay(2000)
            val tabData = model.getTabData()
            onLoadingViewResult()

            // can test with loading error
//            onLoadingError()

            withContext(Dispatchers.IO){

                val titles = arrayListOf<String>()

                tabData.data.forEach {
                    titles.add(it.name)
                    val fragment = AuthorFragment()

                    fragment.arguments = Bundle().apply {
                        putInt("id", it.id)
                    }
                    fragments.add(fragment)
                }

                val titleArr = Array(titles.size) { titles[it] }
                tabDataCompleteLD.postValue(titleArr)

            }

        }

    override fun registerRxBus() {

        errorDis = rxbus.toObservable(RxCode.POST_CODE, Integer::class.java)
            .subscribe {
                if (it.toInt() == RxCode.ERROR_LAYOUT_CLICK_CODE) {
                    getData()
                }
            }

        RxDisposable.add(errorDis)

    }

    override fun removeRxBus() {
        RxDisposable.remove(errorDis)

    }

}


