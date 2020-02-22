@file:Suppress("UNCHECKED_CAST")

package com.crimson.mvvm.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.crimson.mvvm.config.AppConfigOptions
import com.crimson.mvvm.ext.tryCatch
import com.trello.rxlifecycle3.components.support.RxFragment
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author crimson
 * @date 2019/11/15
 * 基类 databinding fragment
 *
 *
 * initView();
 * 抽象方法
 * 与 onCreateView 类似.
 * initViews 是只要 Fragment 被创建就会执行的方法.
 * 也就是说如果我们不想用 LazyLoad 模式
 * 则把所有的初始化 和 加载数据方法都写在 initView 即可.
 *
 *
 * initData();
 * 抽象方法
 * 若将代码写在initData中, 则是在Fragment真正显示出来后才会去Load(懒加载).
 *
 *
 * setForceLoad();
 * 忽略isFirstLoad的值，强制刷新数据，前提是Visible & Prepared.
 * 未Visible & Prepared的页面需要注意在RefreshData的时候视图为空的问题, 具体请参见实例代码
 *
 *
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果
 * 需要先hide再show
 * 需要先hide再show
 * 需要先hide再show
 * eg:
 * transaction.hide(aFragment);
 * transaction.show(aFragment);
 */
abstract class BaseFragment<VB : ViewDataBinding, VM : BaseViewModel> : RxFragment(), IView {

    var vb: VB? = null
    var vm: VM? = null

    var rootLayout: ViewGroup? = null

    var loadingView: IViewDataLoading? = null

    /**
     * 是否可见状态 为了避免和[Fragment.isVisible]冲突 换个名字
     */
    private var isFragmentVisible = false
    /**
     * 标志位，View已经初始化完成。
     * 用isAdded()属性代替
     * isPrepared还是准一些,isAdded有可能出现onCreateView没走完但是isAdded了
     */
    private var isPrepared = false
    /**
     * 是否第一次加载
     */
    private var isFirstLoad = true
    /**
     * <pre>
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     * 一般用于PagerAdapter需要刷新各个子Fragment的场景
     * 不要new 新的 PagerAdapter 而采取reset数据的方式
     * 所以要求Fragment重新走initData方法
    </pre> *
     */
    private var forceLoad = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null && bundle.size() > 0) {
            initVariables(bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViewBindingAndViewModel(inflater, container, savedInstanceState)
        initRootLayout()
        initView()
        isPrepared = true
        lazyLoad()
        return rootLayout
    }

    private fun initRootLayout() {
        context?.apply {
            rootLayout = FrameLayout(this)
            rootLayout?.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            rootLayout?.addView(vb?.root)
        }

    }

    /**
     * init viewBinding and ViewModel
     */
    private fun initViewBindingAndViewModel(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        vb = DataBindingUtil.inflate(
            inflater,
            initContentView(inflater, container, savedInstanceState),
            container,
            false
        )

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
            //ViewDataBinding中设置lifecycleOwner
            lifecycleOwner = this@BaseFragment
            val vmId = initViewModelId()
            setVariable(vmId, vm)
        }
        vm?.run {
            //让ViewModel拥有View的生命周期
            lifecycle.addObserver(this)
            //ViewModel中注入lifecycleOwner
            lifecycleOwner = this@BaseFragment
            //注册RxBus
            registerRxBus()
        }

        initViewModelLiveDataObserver()

    }

    /**
     * 初始化view model中的LiveData call
     */
    private fun initViewModelLiveDataObserver() {

        vm?.onLoadingViewInjectToRootLD?.observe(viewLifecycleOwner, Observer {
            onLoadingViewInjectToRoot()
        })

        vm?.onLoadingViewResultLD?.observe(viewLifecycleOwner, Observer {
            onLoadingViewResult(it ?: false)
        })

        vm?.dataLoadingLD?.observe(viewLifecycleOwner, Observer {
            onDataLoading(it)
        })

        vm?.dataResultLD?.observe(viewLifecycleOwner, Observer {
            onDataResult()
        })

        vm?.dataLoadingErrorLD?.observe(viewLifecycleOwner, Observer {
            onLoadingError()
        })

        vm?.viewFinishedLD?.observe(viewLifecycleOwner, Observer {
            activity?.finish()
        })

    }

    /**
     * run on view create with get data
     */
    fun onLoadingViewInjectToRoot() {
        checkLoadingViewImpl()
        loadingView?.onLoadingViewInjectToRoot(rootLayout)
    }

    /**
     * run on view get data finish
     */
    fun onLoadingViewResult(needEmptyView: Boolean = false) {
        loadingView?.onLoadingViewResult(rootLayout, needEmptyView)
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
        loadingView?.onLoadingError(rootLayout)
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
                    loadingView = constructor.newInstance(context)
                }
            }
            if (loadingView == null) {
                context?.let {
                    loadingView = CommonViewLoading(it)
                }
            }
        }
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable()
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     *
     * @param hidden hidden True if the fragment is now hidden, false if it is not
     * visible.
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            onVisible()
        } else {
            onInvisible()
        }
    }

    protected fun onVisible() {
        isFragmentVisible = true
        lazyLoad()
    }

    protected fun onInvisible() {
        isFragmentVisible = false
    }

    /**
     * 要实现延迟加载Fragment内容,需要在 onCreateView
     * isPrepared = true;
     */
    protected fun lazyLoad() {
        if (isPrepared && isFragmentVisible) {
            if (forceLoad || isFirstLoad) {
                forceLoad = false
                isFirstLoad = false
                initData()
            }
        }
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     * Bundle args = getArguments();
     * if (args != null) {
     * args.putParcelable(KEY, info);
     * }
     */
    fun initVariables(bundle: Bundle?) {}

    /**
     * 忽略isFirstLoad的值，强制刷新数据，但仍要Visible & Prepared
     */
    fun setForceLoad(forceLoad: Boolean) {
        this.forceLoad = forceLoad
    }

    override fun onDestroy() {
        super.onDestroy()
        //解除ViewModel生命周期
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
    abstract fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initViewModelId(): Int

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    open fun initViewModel(): VM? = null

    override fun initView() {}
    override fun initData() {}
    override fun initViewObservable() {}


}