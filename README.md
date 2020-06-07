# MVVM_Module

## 介绍

以MVVM模式为基础的快速集成组件，整合了大量优秀开源项目构建。

## 特点

* 基于LifeCycle+LiveData+ViewModel+DataBinding作为基础结构，可作为项目Base库，快速开发项目
* 支持AndroidX库，集成了AndroidX库下的一些常用组件，如RecyclerView,ViewPager2等
* 提供了Base类(BaseActivity、BaseFragment、BaseViewModel等)统一封装，绑定生命周期，快速进行页面开发
* 对LiveData，协程，RxJava进行了扩展，使用更方便
* 使用Koin容器注入对象，可提供任何对象的依赖注入
* 扩展函数结合DataBinding，使DataBinding使用更方便
* Retrofit封装和扩展，网络请求更方便，提供了协程和RxJava两种方式获取数据方式并通过LiveData处理数据更容易
* RxBus全局处理事件
* 提供全局的Activity，Fragment生命周期管理，提供App统一配置方案
* 提供了简单易用的扩展函数和工具类


## 结构图

![MVVM_Architecture](https://github.com/crimson0829/MVVM_Module/blob/master/architecture/MVVM_Architecture.jpg)

## 引入

application build.gradle :

```
dataBinding {
    enabled true
}
```

```
dependencies {
      
     implementation 'com.github.crimson0829.MVVM_Module:library_mvvm:1.2.2'
    
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

提供了快速创建Activity和Fragment的模版：[模版文件](https://github.com/crimson0829/MVVMModuleTemplate)

## 组件化方案

提供了组件化实现方案，可进行组件化开发：[MVVM_Component](https://github.com/crimson0829/MVVM_Component)

## 使用

1.1 Application初始化：

```
class AppApplication : BaseApplication() {

    override fun onCreate() {

        super.onCreate()

        appConfig()

    }

    /**
     * 设置app_config，全局参数设置，包括状态栏设置，标题栏设置，加载视图设置，Retrofit设置，Glide，toast设置等
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
            .buildGlide(GlideConfig(true, 1.5f, 1.5f, 20, 20))
            .buildToast(ToastConfig(Color.YELLOW, 18f, Color.BLUE))
            .initDefaultSmartRefresh(SmartRefreshHeaderConfig(R.drawable.refresh_head_arrow))
            .initScreenAutoSize()
    }

}
```

1.1.1 如果继承了BaseActivityLifecycle，可重写initActivityLifecycle()方法扩展ActivityLifecycle：

```
  override fun initActivityLifecycle(): ActivityLifecycleCallbacks? {
        //继承BaseActivityLifecycle 的类
        return AppActivityLifecycle()
    }
    
```

1.1.2 如果自己实现加载视图LoadingView,需继承IViewDataLoading 并在AppConfigOptions中全局设置：

```
 AppConfigOptions(context).buildLoadingViewImplClass(<Your LoadingView Impl Class>)
```

1.1.3 如组件需初始化，必须实现IModule接口并在ModuleConfig中声明:

```

//AppModule:

class AppModule :IModule{

}

//ModuleConfig:

//app组件
 private const val APP_MODULE = "com.crimson.mvvm_frame.app.AppModule"

//组件集合
private var modules =
        arrayListOf(
            BASE_MODULE,
            APP_MODULE
        )


```

1.2 Koin 新建 Module 对象，可根据需求自己定制：


```

 factory { TabViewModel() }
 factory { (id: Int) -> AuthorViewModel(id) }

 //单例对象
 single {AuthorModel() }
 
 single {
        get<RetrofitApi>()
            .obtainRetrofit()
            ?.create(AndroidService::class.java)
    }

```

1.3 页面使用:可参考[TabActivity](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/java/com/crimson/mvvm_frame/TabActivity.kt)

1.3.1 layout绑定ViewModel：

```
 <data>
        <variable
            name="viewModel"
            type="com.crimson.mvvm_frame.TabViewModel" />
 </data>

```

1.3.2 View层Activity继承BaseActivity:


```
class TabActivity : BaseActivity<ActivityTabBinding, TabViewModel>() {


    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_tab
    }

    override fun initViewModelId(): Int {
        return BR.viewModel
    }
    
    //也可从Koin容器中获取ViewModel：
     override fun initViewModel(): TabViewModel? {
            return getViewModel()
        }

    override fun initView() {

        vm?.getData()

    }

}

```

1.3.3 设置标题：

```
  override fun initTitleText(): CharSequence? {
        return "欧拉欧拉欧拉"
    }
    
   // 设置标题居中：
  override fun isTitleTextCenter(): Boolean {
        return true
    }
    
    //如果想添加menu，可设置menu：
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
    
  **
    * run with 协程
    */
   fun getData() {
  
       callRemoteLiveDataAsync {
           model.getData()
       }
               //观察livedata
           ?.observe(lifecycleOwner, Observer {
  
               //LiveData.handle() 扩展
           it.handle({
               //when loading
               onLoadingViewInjectToRoot()
  
           },{
               //result empty
               onLoadingViewResult()
  
           },{
               //result error 可做错误处理
               toast("网络错误")
               onLoadingError()
  
           },{_,responseCode->
  
               //result remote error,可根据responseCode做错误提示
               errorResponseCode(responseCode)
               onLoadingError()
  
           },{
               //result success
               onLoadingViewResult()
               runOnIO {
                   handleData(this)
               }
           })
       })
       
  
   }
    
```

1.5 Model层获取数据：


```
    val androidService by inject<AndroidService>()
    
    /**
     * use 协程
     */
    suspend fun getData() = androidService.getTab()

    /**
     * get data use flowable
     */
    fun getAuthorListData(id: Int, page: Int) = androidService.getArticles(id, page).applyThread()
    
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
  class ArticleAdapter : BaseBindingAdapter<ArticleEntity, AdapterItemArticleBinding>
      (R.layout.adapter_item_article) {
  
      override fun convert(holder: BaseViewHolder, item: ArticleEntity?) {
  
          //bind model
          getDataBinding(holder)?.model=item
          
      }
  
  
  }
 ```
 
 2.3 DataBinding扩展函数：提供了Glide，RecyclerView，ViewPager2，SmartRefreshLayout等绑定函数，方便扩展xml和控件调用；

 2.3.1 Glide 绑定 ImageView xml， 可在xml或者View中设置，

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
   <androidx.appcompat.widget.AppCompatImageView
            android:id="@+id/iv_image"
            android:layout_width="50dp"
            android:layout_height="50dp"
            app:imageUrl="@{model.imageUrl}" 
            app:imageStyle="@{1}"
            app:imageRoundShape="@{1}"
            app:image_diskMemoryCache="@{1}"
            app:image_skipMemoryCache="@{false}"
            app:imagePlaceholder="@{@drawablee/icon_picture}"
            app:imageError="@{@drawablee/icon_picture}"         
            />

 
```
2.3.2 RecyclerView绑定：具体使用可参考 xml:[fragment_tab](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/res/layout/fragment_tab.xml)

```

<import type="com.crimson.mvvm.binding.recyclerview.LayoutManagers" />

<import type="com.crimson.mvvm.binding.recyclerview.LineManagers"/>

/**
 * bind recycler view
 * 适配器viewHolder 必须继承 BaseViewHolder
 * rv_adapter：设置适配器
 * rv_layoutManager：设置布局管理器
 * rv_lineManager：设置Item间隔管理器
 * rv_bindScrollStateChanged：滑动状态监听
 * rv_bindScrolled：滑动监听
 */
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:overScrollMode="never"
    app:rv_adapter="@{viewModel.adapter}"
    app:rv_bindScrolled="@{viewModel.bindScrollConsumer}"
    app:rv_lineManager="@{LineManagers.horizontal()}"
    app:rv_layoutManager="@{LayoutManagers.linear()}" />


```

2.3.3 ViewPager2绑定：具体使用可参考TabActivity和xml:[activity_tab](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/res/layout/activity_tab.xml)
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
 
  <androidx.viewpager2.widget.ViewPager2
             android:id="@+id/view_pager"
             android:layout_width="match_parent"
             android:layout_height="match_parent"
             android:orientation="horizontal"
             app:vp2_bindPageScrolled="@{viewModel.vp2ScrolledConsumer}"
             app:vp2_multiPagePadding="@{40}"
             app:vp2_transformerType="@{2}"
             app:vp2_bindPageSelected="@{viewModel.vp2SelectedConsumer}"
              />


```

2.3.4 SmartRefreshLayout绑定：具体使用可参考[AuthorViewModel](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/java/com/crimson/mvvm_frame/AuthorFragment.kt)和xml:[fragment_tab](https://github.com/crimson0829/MVVM_Module/blob/master/sample/src/main/res/layout/fragment_tab.xml)

```

/**
 * bind smart refresh
 * sm_bindRefresh：绑定下拉刷新监听
 * sm_bindLoadMore：绑定上拉加载监听
 */

    <com.scwang.smartrefresh.layout.SmartRefreshLayout
        android:id="@+id/refresh_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:sm_bindLoadMore="@{viewModel.loadMoreConsumer}"
        app:sm_bindRefresh="@{viewModel.refreshConsumer}"
        app:srlEnableAutoLoadMore="true">
     </com.scwang.smartrefresh.layout.SmartRefreshLayout>    
     
    //viewModel中绑定
    //bind refresh
    val refreshConsumer = bindConsumer<RefreshLayout> { }

    //bind loadmore
    val loadMoreConsumer =bindConsumer<RefreshLayout> {  }

             

```
2.3.5 TextView 绑定
```
/**
 * bind text changes
 * textChanges：绑定文字改变监听
 * keyboardSearch：键盘搜索监听
 */
    <androidx.appcompat.widget.AppCompatEditText
                 android:layout_width="math_parent"
                 android:layout_height="50dp"
                 app:textChanges="@{viewModel.textChanged}" 
                 app:keyboardSearch="@{viewModel.keyboardSearch}"
                 />
                 
     //viewModel中绑定
    val textChanged = bindConsumer<String> { }
    val keyboardSearch =bindConsumer<Any> {  }             
                 
```

2.3.6 View 点击绑定：

```
/**
 * bind click
 * bindClick：绑定点击
 * clickDuration：点击事件间隔
 *bindLongClick：绑定长按点击
 */

<View
 android:layout_width="wrap_content"
 android:layout_height="wrap_content" 
 app:bindClick="@{viewModel.viewClick}"
 app:clickDuration="@{200}"
 app:bindLongClick="@{viewMoedl.viewLongClick}"
 />


```

2.3.7 SwichCompat 监听绑定：

```
  <androidx.appcompat.widget.SwitchCompat
                android:layout_width="wrap_content"
                app:checkChanged="@{viewModel.checkedChanged}"
                android:layout_height="wrap_content" />
  
  //viewModel中绑定              
  val checkedChanged = bindConsumer<Boolean> {}

```


3.1 网络请求：使用


```

//全局设置retrofit: 设置可参考RetrofitConfig；目前可设置baseUrl,connectTime,responseLog,requestLog和http headers
AppConfigOptions(context).buildRetrofit()

//获取Retrofit和获取OkHttpClient:
  NetworkClient.get(androidContext()).obtainRetrofit()
  NetworkClient.get(androidContext()).obtainOkHttp()
  
//or
val retrofit by inject<Retrofit>()
val okHttp by inject<OkHttpClient>()

//获取数据：
//获取Retrofit Service
 val androidService by inject<AndroidService>()
 
 androidService.getArticles(id, page)
              //扩展函数线程切换 从IO线程切换到主线程
             .applyThread()

```


更多Api调用可参考[NetworkClient](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/net/NetworkClient.kt) 

4.1 数据处理：

协程处理数据：提供了协程扩展类[CoroutineExt.kt](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/coroutines/CoroutineExt.kt)和[CoroutineExt2.kt](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/coroutines/CoroutineExt2.kt);
可在ViewModel中处理数据；用同步的代码实现异步操作-_-

```
//异步获取远程数据
callRemoteLiveDataAsync{
    //getData
   model.getDdata()
   
   //handle Data

}
```
RxJava处理数据：提供了RxJava扩展类[RxJavaExt.kt](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/rx/RxJavaExt.kt);
可将数据转换成LiveData操作

```
  //异步获取远程数据
  Flowable
      //绑定生命周期
    .bindToLifecycle(lifecycleOwner)
    //转化成LiveData 处理数据
    .callRemotePost(LiveData().apply {
        observe(lifecycleOwner, Observer {
            //or execute it.handle()
            when (it) {
                //result success
                is RetrofitResult.Success -> {
                
                }
                //when loading
                RetrofitResult.Loading -> {
                    
                }
                //result empty
                RetrofitResult.EmptyData -> {
                  
                }
                //result error
                is RetrofitResult.Error -> {
                   
                }
                //result remote error
                is RetrofitResult.RemoteError -> {
                   
                }
            }
 
        })
    })

```

4.2 [RxBus](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/rx/bus/RxBus.kt)全局事件处理:由[RxDisposable](https://github.com/crimson0829/MVVM_Module/blob/master/mvvm_library/src/main/java/com/crimson/mvvm/rx/bus/RxDisposable.kt)统一管理订阅事件

订阅事件：在ViewModel中订阅和移除

```
方法1：
 dispose=rxbus.toObservable(<对应code>,<对应类型>)
            .subscribe {
               //这里处理逻辑
            }
            
 //or 
  dispose=rxbus.toObservable(<对应类型>)
             .subscribe {
                //这里处理逻辑
             }
  
  //添加事件     
 RxDisposable.add(dispose)  
 
 //移除事件
 RxDisposable.remove(dispose)   

方法2：绑定生命周期

rxbus.toObservable(<对应code>,<对应类型>)
            .bindToLifecycle(lifecycleOwner)
            .subscribe {
               //这里处理逻辑
            }
                 
```

发送事件：

```
 RxBus.get().post(<对应code>, <对应类型>)
 
 //or
  RxBus.get().send(<对应类型>)
```


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


