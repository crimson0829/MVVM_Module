# MVVM_Module

## 介绍

以MVVM模式为基础的快速集成组件,整合了大量优秀开源项目构建。

## 特点

1.Kotlin开发，基于AAC+DataBinding作为基础结构，可作为项目Base库，快速开发项目
<br>
2.支持AndroidX库，集成了AndroidX库下的一些常用组件，如RecyclerView,ViewPager2等
<br>
3.提供了Base类(BaseActivity、BaseFragment、BaseViewModel等)统一封装，绑定生命周期，快速进行页面开发
<br>
4.使用Koin容器注入对象,可提供任何对象的依赖注入
<br>
5.Kotlin扩展函数结合DataBinding，使DataBinding使用更方便
<br>
6.提供全局的Activity,Fragment生命周期管理，提供App统一配置方案
<br>
7.Retrofit封装，网络请求更方便，提供了协程和RxJava两种方式获取数据方式，具体实现可参照 sample
<br>
8.RxBus全局处理事件
<br>


## 引入

application build.gradle :

```
dataBinding {
    enabled true
}
```

```
dependencies {
      
      implementation "com.github.crimson0829:mvvm_library:1.1.0"
    
}
```

根目录 build.gradle :

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

## 模版开发

提供了快速创建Activity和Fragment的模版(懒惰才是第一生产力-_-)：

[模版文件](https://github.com/crimson0829/MVVMModuleTemplate)

## 使用

1.1 Application初始化：

```
class AppApplication : BaseApplication() {

    override fun onCreate() {

        //添加新的module，必须在super前调用
        injectKoinModules(viewModelModule, modelModule, adapterModule, dataModule)

        super.onCreate()

        appConfig()

    }

    /**
     * 设置app_config，全局参数设置，包括状态栏设置，标题栏设置，加载视图设置，Retrofit设置等
     * more config will be add
     */
    private fun appConfig() {
        AppConfigOptions(this)
        //状态栏设置，可设置背景颜色，是否亮色模式和透明度
        //  .buildStatusBar(StatusBarConfig(R.color.colorPrimary,false,100))
        //标题栏设置，可设置背景色 返回图标 字体颜色 字体大小 和是否居中显示
        //  .buildTitleBar(TitleBarConfig(R.color.colorPrimary,R.drawable.app_back_icon,
        //     Color.parseColor("#ffffff"),16f,true))
            .buildLoadingViewImplClass(CommonViewLoading::class.java)
            .buildRetrofit(RetrofitConfig(AndroidService.BASE_URL, 20))
            .initDefaultSmartRefresh(SmartRefreshHeaderConfig(R.drawable.refresh_head_arrow))
            .initScreenAutoSize()
    }

}
```

如果继承了BaseActivityLifecycle，可重写initActivityLifecycle()方法扩展ActivityLifecycle：

```
  override fun initActivityLifecycle(): ActivityLifecycleCallbacks? {
        //继承BaseActivityLifecycle 的类
        return AppActivityLifecycle()
    }
    
```

如果自己实现加载视图LoadingView,需继承IViewDataLoading 并在AppConfigOptions中全局设置：

```
 AppConfigOptions(context).buildLoadingViewImplClass(<Your LoadingView Impl Class>)
```


1.2 Koin 新建 Module 对象，可根据需求自己定制：


```
val viewModelModule = module {

    factory { TabViewModel() }
    factory { (id: Int) -> AuthorViewModel(id) }

}

val modelModule= module {

    single {
        AuthorModel()
    }

}

val adapterModule= module {

    factory {
        ArticleAdapter()
    }
}

val dataModule= module {

    single {
        get<RetrofitApi>()
            .obtainRetrofit()
            ?.create(AndroidService::class.java)
    }
}

```

1.3 页面使用:可参考[TabActivity](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/java/com/crimson/mvvm_frame/TabActivity.kt)

layout绑定ViewModel：

```
 <data>
        <variable
            name="viewModel"
            type="com.crimson.mvvm_frame.TabViewModel" />
 </data>

```

View层Activity继承BaseActivity:


```
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

}

```

也可从Koin容器中获取ViewModel：

```
  override fun initViewModel(): TabViewModel? {
        return getViewModel()
    }
    
```

设置标题：

```
  override fun initTitleText(): CharSequence? {
        return "欧拉欧拉欧拉"
    }
    
   // 设置标题居中：
  override fun isTitleTextCenter(): Boolean {
        return true
    }
```

如果想添加menu，可设置menu：

```

   // 初始化menu布局
  override fun initMenuRes(): Int? {
        return R.menu.tab_menu
        
  }
  
   //menu点击回调   
  override fun onMenuItemSelected(item: MenuItem) {
         when (item.itemId) {
               R.id.page_refresh -> {
                  logw("refresh page")
                  toast("refresh page")
               }
         }
  }        
```


1.4 ViewModel层接收数据并处理逻辑,最后通过LiveData通知View层展示页面;
<br>
TabViewModel:


```
    //koin inject
    val model by inject<AuthorModel>()

    //live data
    val tabDataCompleteLD by inject<SingleLiveData<Array<String>>>()

    val fragments = arrayListOf<Fragment>()
    
    //xml中viewpager2绑定
    val vp2SelectedConsumer = bindConsumer<Int> {
    
            logw("vp2page -> $this")
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
           
```

[RxBus](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/rx/bus/RxBus.kt)注册：

```
override fun registerRxBus() {

        errorDis = rxbus.toObservable(RxCode.POST_CODE, Integer::class.java)
            .subscribe {
                if (it.toInt() == RxCode.ERROR_LAYOUT_CLICK_CODE) {
                    getData()
                }
            }

        RxDisposable.add(errorDis)

    }
```

1.5 Model层获取数据：


```
    val androidService by inject<AndroidService>()

    /**
     * get data use coroutine
     */
    suspend fun getTabData(): TabListEntity {
        return callRemoteTabData { androidService.getTab() }
    }

    private suspend fun callRemoteTabData(call: suspend () -> TabListEntity): TabListEntity {
        return withContext(Dispatchers.IO) { call.invoke() }
    }
    
```

1.6 View层展示数据：bindAdapter和bindTabLayout为ViewPager2扩展函数


```
       vm?.tabDataCompleteLD?.observe(this, Observer { it ->
   
               vb?.viewPager?.apply {
   
                   vm?.fragments?.let {
                       //设置viewpager2 adapter
                       bindAdapter(null,ViewPager2FragmentAdapter(this@TabActivity, it))
                   }
   
                   bindTabLayout( vb?.tabLayout,it)
   
               }
   
           })

```

<br>

2.1 DataBinding xml与ViewModel双向绑定：

xml：

```

    <variable
            name="viewModel"
            type="com.crimson.mvvm_frame.TabViewModel" />
            
            
   app:vp2_bindPageScrolled="@{viewModel.vp2ScrolledConsumer}"
  
```
ViewModel:
```
    val vp2SelectedConsumer = bindConsumer<Int> {
  
          logw("vp2page -> $this")
      }

```

2.2 DataBinding xml与RecyclerViewAdapter双向绑定

 xml:
 ```
  <variable
             name="model"
             type="com.crimson.mvvm_frame.model.kdo.ArticleEntity" />
             
             
  android:text="@{model.author}"
```
在adapter中设置:
 ```
  helper.getBinding<AdapterItemArticleBinding>()?.model = item
 ```
 
 2.3 DataBinding扩展函数：提供了Glide，RecyclerView，ViewPager2，SmartRefreshLayout等绑定函数，方便扩展xml和控件调用；实现类[ViewBindingsExt](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/binding/ViewBindingsExt.kt)
 
 Glide绑定图片：可在xml或者View中设置，具体使用可参考[PictureActivity](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/java/com/crimson/mvvm_frame/PictureActivity.kt)
 
```
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
```
RecyclerView绑定：具体使用可参考 xml:[fragment_tab](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/res/layout/fragment_tab.xml)

```
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
```

ViewPager2绑定：具体使用可参考TabActivity和xml:[activity_tab](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/res/layout/activity_tab.xml)
```
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
                    if (it.size > position) {
                        tab.text = it[position]
                    }
                }
            })
            .attach()
    }

}
```

SmartRefreshLayout绑定：具体使用可参考[AuthorViewModel](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/java/com/crimson/mvvm_frame/AuthorFragment.kt)和xml:[fragment_tab](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/res/layout/fragment_tab.xml)

```

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

```

View的点击绑定：

```
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

```
3.1 网络请求：

全局设置retrofit: 设置可参考RetrofitConfig；目前可设置baseUrl,connectTime,responseLog,requestLog和http headers

```
AppConfigOptions(context).buildRetrofit()

```

获取Retrofit:

```
  RetrofitApi.get(androidContext()).obtainRetrofit()
```
直接通过Koin获取：
```
 val retrofit by inject<Retrofit>()
```
获取OkHttpClient:
```
 RetrofitApi.get(androidContext()).obtainOkHttp()
```
直接通过Koin获取：
```
  val okHttp by inject<OkHttpClient>()
```
获取数据：
```
 //获取Retrofit Service
 val androidService by inject<AndroidService>()
 
 androidService.getArticles(id, page)
              //扩展函数线程切换 从IO线程切换到主线程
             .applyThread()
```

更多Api调用可参考[RetrofitApi](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/net/RetrofitApi.kt) [AuthorModel](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/java/com/crimson/mvvm_frame/model/AuthorModel.kt)

4.1 数据处理：

协程处理数据：ViewModel中处理数据；用同步的代码实现异步操作-_-

```
//主线程处理
 launchCoroutine {
 
 //获取数据
 
 //IO子线程处理
   withContext(Dispatchers.IO) {
   
   }
 
 }
```
RxJava处理数据：
```
  //获取数据
  model.getAuthorListData(id, page)
            //绑定生命周期
            .bindToLifecycle(lifecycleOwner)
            //onStart
            .doOnSubscribe {
                //can do something  before subscribe
                //can show loading view
                logw("doOnSubscribe -> onStart")
                if (page == 1 && !refresh) {
                    onLoadingViewInjectToRoot()
                }

            }
            //扩展函数，实现了请求出错自动弹出toast的操作
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
```

subscribeNet扩展实现：

```
/**
 * 网络请求统一订阅处理，目前只处理了报错弹出toast的情况
 */

private val onNextStub: (Any) -> Unit = {}

//重写rxkotlin,全局toast异常
private val onErrorStub: (Throwable?) -> Unit = {
    loge(it?.message)
    //全部甩锅网络异常
    toast("网络异常")
}
private val onCompleteStub: () -> Unit = {}


fun <T : Any> Flowable<T>.subscribeNet(
    onError: (Throwable) -> Unit = onErrorStub,
    onComplete: () -> Unit = onCompleteStub,
    onNext: (T) -> Unit = onNextStub
): Disposable = subscribe(onNext, onError, onComplete)

```
4.2 [RxBus](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/rx/bus/RxBus.kt)全局事件处理:由[RxDisposable](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/rx/bus/RxDisposable.kt)统一管理订阅事件

订阅事件：在ViewModel中订阅和移除

```
 dispose=RxBus.get().toObservable(<对应code>,<对应类型>)
            .subscribe {
               //这里处理逻辑
            }
            
 //or 
  dispose=RxBus.get().toObservable(<对应类型>)
             .subscribe {
                //这里处理逻辑
             }
  
  //添加事件     
 RxDisposable.add(dispose)  
 
 //移除事件
 RxDisposable.remove(dispose)   
                 
```

发送事件：

```
 RxBus.get().post(<对应code>, <对应类型>)
 
 //or
  RxBus.get().send(<对应类型>)
```

5.1 一些扩展类和工具类
<br>
[AppExt：常用的扩展和顶层函数](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/ext/AppExt.kt)
<br>
[LogExt：Timber实现，log顶层函数](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/ext/LogExt.kt)
<br>
[ToastExt：全局Toast顶层函数](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/ext/ToastExt.kt)
<br>
[LiveDataExt：将LiveData转化成Flowable的扩展函数](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/livedata/LiveDataExt.kt)
<br>
[CacheCleanUtils：缓存清理工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/CacheCleanUtils.kt)
<br>
[CaptureUtils：截图工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/CaptureUtils.kt)
<br>
[ConvertUtils：转换工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/ConvertUtils.kt)
<br>
[FileUtils：文件工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/FileUtils.kt)
<br>
[GsonUtils：Gson工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/GsonUtils.kt)
<br>
[IOUtils：IO工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/IOUtils.kt)
<br>
[KeyBoardUtils：键盘工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/KeyBoardUtils.kt)
<br>
[NetWorkUtils：网络工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/NetWorkUtils.kt)
<br>
[PermissionUtils：权限工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/PermissionUtils.kt)
<br>
[RegexUtils：正则工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/RegexUtils.kt)
<br>
[RoomUtils：系统Room相关工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/RoomUtils.kt)
<br>
[ScreenUtils：屏幕工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/ScreenUtils.kt)
<br>
[SDKVersionUtils：Android SDK版本工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/SDKVersionUtils.kt)
<br>
[StatusBarUtils：状态栏工具类](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/utils/StatusBarUtils.kt)
<br>


## 使用的官方库和开源库

1.[AndroidX库：包括Appcompat、Lifecycle、RecyclerView、Viewpager2、Google_Material等](https://developer.android.com/jetpack/androidx/versions/stable-channel)
<br>
2.[Koin：轻量级的依赖注入框架，无代理，无代码生成，无反射，比Dagger2简洁点-_-](https://github.com/InsertKoinIO/koin)
<br>
3.[RxJava：大名鼎鼎-_-](https://github.com/ReactiveX/RxJava)
<br>
4.[RxAndroid](https://github.com/ReactiveX/RxAndroid)
<br>
5.[RxLifecycle](https://github.com/trello/RxLifecycle)
<br>
6.[RxPermissions](https://github.com/tbruyelle/RxPermissions)
<br>
7.[RxBinding](https://github.com/JakeWharton/RxBinding)
<br>
8.[RxKotlin:RxJava在Kotlin上的扩展库](https://github.com/ReactiveX/RxKotlin)
<br>
9.[Retrofit](https://github.com/square/retrofit)
<br>
10.[OkHttp](https://github.com/square/okhttp)
<br>
11.[Glide](https://github.com/bumptech/glide)
<br>
12.[Gson](https://github.com/google/gson)
<br>
13.[Timber](https://github.com/JakeWharton/timber)
<br>
14.[BaseRecyclerViewAdapterHelper：非常好用的RecyclerViewAdapter的封装库](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
<br>
15.[SmartRefreshLayout：非常好用的下拉刷新框架](https://github.com/scwang90/SmartRefreshLayout)
<br>
16.[AndroidAutoSize：屏幕适配解决方案，思想值得借鉴](https://github.com/JessYanCoding/AndroidAutoSize)
<br>
17.[Material-Dialogs](https://github.com/afollestad/material-dialogs)
<br>
18.[LeakCanary](https://github.com/square/leakcanary)
<br>
19.[感谢wanandroid提供的Api接口，感谢大佬](https://github.com/hongyangAndroid/wanandroid)


## License

```
Copyright 2019 crimson0829
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


