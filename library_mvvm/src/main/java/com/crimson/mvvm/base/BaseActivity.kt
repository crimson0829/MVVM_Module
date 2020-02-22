@file:Suppress("UNCHECKED_CAST")

package com.crimson.mvvm.base

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.ext.tryCatch
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author crimson
 * @date 19/12/15
 * base Activity
 * 默认单个ViewModel,如果想一个activity有多个ViewModel,可以用viewModelModule创建viewModel并注入
 * 全局默认设置了状态栏和标题栏，如果想自己定制，可重写对应方法
 */
abstract class BaseActivity<VB : ViewDataBinding, VM : BaseViewModel> : RxAppCompatActivity(),
    IView, IStatusBar, ITitleBar {

    var vb: VB? = null

    var vm: VM? = null

    var loadingView: IViewDataLoading? = null

    protected val context: Context by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {

        //初始化ViewDataBinding和ViewModel
        initViewBindingAndViewModel(savedInstanceState)
        //这里调用super就可以在BaseActivityLifecycle中获取contentView,从而对布局View进行操作
        super.onCreate(savedInstanceState)

    }

    /**
     * 初始化ViewBinding 和 ViewModel
     */
    private fun initViewBindingAndViewModel(savedInstanceState: Bundle?) {

        vb = DataBindingUtil.setContentView(this, initContentView(savedInstanceState))
        vm = initViewModel()
        if (vm == null) {
            val type: Type? = javaClass.genericSuperclass
            if (type is ParameterizedType && type.actualTypeArguments.size == 2) {
                tryCatch {
                    val viewModel = type.actualTypeArguments[1] as? Class<VM>
                    vm = viewModel?.newInstance()
                }
            }
        }

        vb?.run {
            lifecycleOwner = this@BaseActivity
            val vmId = initViewModelId()
            setVariable(vmId, vm)
        }

        //关联ViewModel
        vm?.run {
            //让ViewModel拥有View的生命周期
            lifecycle.addObserver(this)
            //ViewModel中注入lifecycleOwner
            lifecycleOwner = this@BaseActivity
            //注册RxBus
            registerRxBus()
        }

        initViewModelLiveDataObserver()

    }

    /**
     * 初始化view model中的LiveData call
     */
    private fun initViewModelLiveDataObserver() {

        vm?.onLoadingViewInjectToRootLD?.observe(this, Observer {
            onLoadingViewInjectToRoot()
        })

        vm?.onLoadingViewResultLD?.observe(this, Observer {
            onLoadingViewResult(it?:false)
        })

        vm?.dataLoadingLD?.observe(this, Observer {
            onDataLoading(it)
        })

        vm?.dataResultLD?.observe(this, Observer {
            onDataResult()
        })

        vm?.dataLoadingErrorLD?.observe(this, Observer {
            onLoadingError()
        })

        vm?.viewFinishedLD?.observe(this, Observer {
            finish()
        })

    }

    /**
     * run on view create with get data
     */
    fun onLoadingViewInjectToRoot() {
        checkLoadingViewImpl()
        loadingView?.onLoadingViewInjectToRoot(vb?.root?.parent as? ViewGroup)
    }

    /**
     * run on view get data finish
     */
    fun onLoadingViewResult(needEmptyView: Boolean) {
        loadingView?.onLoadingViewResult(vb?.root?.parent as? ViewGroup, needEmptyView)
    }

    /**
     * run on data loading
     */
    fun onDataLoading(it: String?) {
        checkLoadingViewImpl()
        loadingView?.onDataLoading(it)

    }

    /**
     * run on data loading finish
     */
    fun onDataResult() {
        loadingView?.onDataLoadingResult()
    }


    /**
     * data loading error
     */
    fun onLoadingError() {
        checkLoadingViewImpl()
        loadingView?.onLoadingError(vb?.root?.parent as? ViewGroup)
    }

    /**
     * 检查loadingView的实现
     * 如果想定制化单个页面的LoadingView，可重写该方法实现
     */
    open fun checkLoadingViewImpl() {
        if (loadingView == null) {
            val clazz = AppConfigOptions.LOADING_VIEW_CLAZZ
            if (clazz != null) {
                tryCatch {
                    val constructor = clazz.getConstructor(Context::class.java)
                    loadingView = constructor.newInstance(this)
                }
            }
            if (loadingView == null) {
                loadingView = CommonViewLoading(this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        initMenuRes()?.let {
            if (it != 0) {
                menuInflater.inflate(it, menu)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        initMenuRes()?.let {
            if (it != 0) {
                onMenuItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除ViewModel生命周期
        vm?.let {
            lifecycle.removeObserver(it)
            it.removeRxBus()
            null
        }
        vb?.apply {
            unbind()
        }
        loadingView?.let {
            null
        }

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(savedInstanceState: Bundle?): Int

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initViewModelId(): Int

    /**
     * 初始化viewModel
     */
    open fun initViewModel(): VM? = null

    /**
     * 初始化statusBar，可重写该方法并返回true可自己消费；默认为false，已经在BaseActivityLifecycle中实现
     */
    override fun initStatusBar(): Boolean = false

    /**
     * 初始化titleBar,可重写该方法并返回true为自己消费,下面所有的默认titleBar设置将失效，默认已经在BaseActivityLifecycle中实现
     */
    override fun initTitleBar(): Boolean = false

    /**
     * 设置返回按钮图标，如果没有全局设置就默认使用系统实现
     */
    override fun initBackIconRes(): Int = AppConfigOptions.TITLE_BAR_CONFIG.backIcon

    /**
     * 重写该方法可设置标题，默认为上个页面传值，如果不传就显示空标题
     */
    override fun initTitleText(): CharSequence? = intent.getStringExtra(ITitleBar.VIEW_TITLE) ?: ""

    /**
     * 标题是否居中，默认false
     */
    override fun isTitleTextCenter(): Boolean = AppConfigOptions.TITLE_BAR_CONFIG.titleIsCenter

    /**
     * 初始化menu布局
     */
    override fun initMenuRes(): Int? = 0

    /**
     * menu布局条目选中点击
     */
    override fun onMenuItemSelected(item: MenuItem) {}

    /**
     * 在BaseActivityLifecycle已全局调用
     */
    override fun initView() {}

    override fun initData() {}
    override fun initViewObservable() {}


}






