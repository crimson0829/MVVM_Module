package com.crimson.mvvm_frame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.Companion.DEFAULT_BEHAVIOR
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.crimson.mvvm.base.BaseFragment
import com.crimson.mvvm.base.BaseViewModel
import com.crimson.mvvm.binding.consumer.bindConsumer
import com.crimson.mvvm.binding.consumer.bindTiConsumer
import com.crimson.mvvm.ext.logw
import com.crimson.mvvm.livedata.SingleLiveData
import com.crimson.mvvm.net.RetrofitResult
import com.crimson.mvvm.rx.callRemotePost
import com.crimson.mvvm_frame.databinding.FragmentTabBinding
import com.crimson.mvvm_frame.model.AuthorModel
import com.crimson.mvvm_frame.model.kdo.AuthorListEntity
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

    override fun initViewModelId(): Int = BR.viewModel

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

    val bindScrollConsumer =
        bindTiConsumer<RecyclerView, Int, Int> { rv, dx, dy ->
            logw("dx -> $dx dy -> $dy")
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
    val refreshConsumer =
        bindConsumer<RefreshLayout> {
            refreshLayout = this
            refresh = true
            page = 1
            getArticles()

        }

    //bind loadmore
    val loadMoreConsumer =
        bindConsumer<RefreshLayout> {
            refresh = false
            refreshLayout = this
            page++
            getArticles()
        }


    /**
     * rxJava 获取数据
     */
    fun getArticles() {

        model.getAuthorListData(id, page)
            .bindToLifecycle(lifecycleOwner)
            .callRemotePost(MutableLiveData<RetrofitResult<AuthorListEntity>>().apply {
                observe(lifecycleOwner, Observer {
                    //or execute it.handle()
                    when (it) {
                        //result success
                        is RetrofitResult.Success -> {

                            finishLoading()
                            if (page == 1 && !refresh) {
                                onLoadingViewResult()
                            }

                            if (page == 1) {
                                adapter.data.clear()
                            }
                            adapter.addData(it.value.data.datas)


                        }
                        //when loading
                        RetrofitResult.Loading -> {
                            logw("doOnSubscribe -> onStart")
                            if (page == 1 && !refresh) {
                                onLoadingViewInjectToRoot()
                            }
                        }

                        //result empty
                        RetrofitResult.EmptyData -> {
                            finishLoading()
                            if (page == 1) {
                                onLoadingViewResult()

                            }

                        }
                        //result error
                        is RetrofitResult.Error -> {
                            finishLoading()
                            if (page == 1) {
                                onLoadingError()
                            }
                        }
                        //result remote error
                        is RetrofitResult.RemoteError -> {
                            finishLoading()
                            if (page == 1) {
                                onLoadingError()
                            }
                        }
                    }

                })
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


