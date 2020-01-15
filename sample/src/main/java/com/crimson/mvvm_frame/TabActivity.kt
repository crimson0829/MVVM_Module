package com.crimson.mvvm_frame

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.crimson.mvvm.base.BaseActivity
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.adapter.ViewPager2FragmentAdapter
import com.crimson.mvvm.binding.bindAdapter
import com.crimson.mvvm.binding.bindConsumer
import com.crimson.mvvm.binding.bindTabLayout
import com.crimson.mvvm.binding.bindTiConsumer
import com.crimson.mvvm.ext.logw
import com.crimson.mvvm.ext.toast
import com.crimson.mvvm.livedata.SingleLiveData
import com.crimson.mvvm.rx.bus.RxCode
import com.crimson.mvvm.rx.bus.RxDisposable
import com.crimson.mvvm_frame.databinding.ActivityTabBinding
import com.crimson.mvvm_frame.model.AuthorModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
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

    override fun initViewModel(): TabViewModel? {
        return getViewModel()
    }

   override fun initTitleText(): CharSequence? {
        return "欧拉欧拉欧拉"
    }

    override fun isTitleTextCenter(): Boolean {
        return true
    }

    override fun initMenuRes(): Int? {
        return R.menu.tab_menu
    }

    override fun onMenuItemSelected(item: MenuItem) {
        when(item.itemId){
            R.id.page_refresh-> {
                logw("refresh page")
                toast("refresh page")
            }
        }
    }

    override fun initView() {

        vm?.getData()

    }


    override fun initViewObservable() {


        vm?.tabDataCompleteLD?.observe(this, Observer { it ->

            vb?.viewPager?.apply {

                vm?.fragments?.let {
                    //设置viewpager2 adapter
                    bindAdapter(null,ViewPager2FragmentAdapter(this@TabActivity, it))
                }

                bindTabLayout( vb?.tabLayout,it)


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
    val tabDataCompleteLD by inject<SingleLiveData<MutableList<String>>>()

    val fragments = arrayListOf<Fragment>()

    var errorDis: Disposable? = null

    val vp2SelectedConsumer= bindConsumer<Int> {

        logw("vp2page -> $this")
    }

    val vp2ScrolledConsumer= bindTiConsumer<Int,Float,Int> { t1, t2, t3 ->
        logw("vp2pos -> $t1 positionOffset->$t2 positionOffsetPixels -> $t3")
    }



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

            withContext(Dispatchers.IO) {

                val titles = arrayListOf<String>()

                tabData.data.forEach {
                    titles.add(it.name)
                    val fragment = AuthorFragment()

                    fragment.arguments = Bundle().apply {
                        putInt("id", it.id)
                    }
                    fragments.add(fragment)
                }

                tabDataCompleteLD.postValue(titles)

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


