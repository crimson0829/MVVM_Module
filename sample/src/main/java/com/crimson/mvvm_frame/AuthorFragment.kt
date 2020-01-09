package com.crimson.mvvm_frame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.Companion.DEFAULT_BEHAVIOR
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.crimson.mvvm.base.BaseFragment
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.bindBiConsumer
import com.crimson.mvvm.binding.bindConsumer
import com.crimson.mvvm.ext.logw
import com.crimson.mvvm.livedata.SingleLiveData
import com.crimson.mvvm.net.subscribeNet
import com.crimson.mvvm_frame.databinding.FragmentTabBinding
import com.crimson.mvvm_frame.model.AuthorModel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf


/**
 * @author crimson
 * @date   2020-01-02
 * fragment sample
 */
class AuthorFragment : BaseFragment<FragmentTabBinding, AuthorViewModel>() {


    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int = R.layout.fragment_tab

    override fun initVariableId(): Int = BR.viewModel

    override fun initViewModel(): AuthorViewModel? {

        arguments?.takeIf { it.containsKey("id") }?.apply {
            val id = getInt("id")
            return getViewModel { parametersOf(id) }
        }

        return null
    }

    override fun initView() {
        vm?.getArticles()

    }

}

/**
 * data from rxjava
 */
class AuthorViewModel(val id: Int) : BaseViewModel() {

    var page = 1

    var refresh = false

    //lazy init
    val model: AuthorModel by inject()

    //即时初始化
    val adapter = get<ArticleAdapter>().apply {
        setOnItemClickListener { _, _, position ->

            val entity = this.data[position]
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(entity?.link)))

            showWebViewDialog(entity?.link, position)


        }

    }

    val bindScrollConsumer = bindBiConsumer<Int, Int> { t1, t2 ->
        logw("dx -> $t1 dy -> $t2")
    }


    //test liveData
    val refreshFinishLD by inject<SingleLiveData<Int>>()

    //test refreshlayout
    var refreshLayout: RefreshLayout? = null

    @SuppressLint("SetJavaScriptEnabled")
    private fun showWebViewDialog(url: String? = "", position: Int) {
        context()?.apply {
            MaterialDialog(this, if (position % 2 == 0) BottomSheet() else DEFAULT_BEHAVIOR)
                .show {
                    customView(R.layout.custom_web, noVerticalPadding = true)
                    debugMode(false)
                }.also {
                    it.onShow {
                        val webView: WebView = it.getCustomView()
                            .findViewById(R.id.web_view)

                        webView.apply {

                            settings.run {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                useWideViewPort = true
                            }

                            loadUrl(url)

                        }
                    }
                }
        }
    }


    //bind refresh
    val refreshConsumer = bindConsumer<RefreshLayout> {
        refreshLayout = this
        refresh = true
        page = 1
        getArticles()
    }

    //bind loadmore
    val loadMoreConsumer = bindConsumer<RefreshLayout> {
        refresh = false
        refreshLayout = this
        page++
        getArticles()
    }


    fun getArticles() {

        model.getAuthorListData(id, page)
            .bindToLifecycle(lifecycleOwner)
            .doOnSubscribe {
                //can do something  before subscribe
                //can show loading view
                logw("doOnSubscribe -> onStart")
                if (page == 1 && !refresh) {
                    onLoadingViewInjectToRoot()
                }

            }
            .subscribeNet({
                finishLoading()
                if (page == 1) {
                    onLoadingError()
                }
            }, {
                finishLoading()
                if (page == 1 && !refresh) {
                    onLoadingViewResult()
                }
            }, {
                if (page == 1) {
                    adapter.data.clear()
                }
                adapter.addData(it.data.datas)
            })


    }

    private fun finishLoading() {
        if (page == 1) {
            refreshLayout?.finishRefresh(0)
        } else {
            refreshLayout?.finishLoadMore(0)
        }
    }


}


