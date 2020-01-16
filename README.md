# MVVM_Library

## 介绍

以 MVVM 模式为基础的快速集成框架,整合了大量优秀开源项目构建。

## 特点

1.可作为项目Base库，快速开发项目
<br>
2.支持AndroidX库，集成了AndroidX库下的一些常用组件，如AppCompat,RecyclerView等
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

```
dependencies {
      
      implementation "com.github.crimson0829:mvvm_library:1.1.0"
    
}
```

## 使用

Application初始化

```
class AppApplication : BaseApplication() {

    override fun onCreate() {

        //添加新的module，必须在super前调用
        injectKoinModules(viewModelModule, modelModule, adapterModule, dataModule)

        super.onCreate()

        appConfig()

    }

    /**
     * 设置app_config
     * more config will be add
     */
    private fun appConfig() {
        AppConfigOptions(this)
        //  .buildStatusBar(StatusBarConfig(R.color.colorPrimary,false,100))
        //  .buildTitleBar(TitleBarConfig(R.color.colorPrimary,R.drawable.app_back_icon,
        //     Color.parseColor("#ffffff"),16f,true))
            .buildLoadingViewImplClass(CommonViewLoading::class.java)
            .buildRetrofit(RetrofitConfig(AndroidService.BASE_URL, 20))
            .initDefaultSmartRefresh(SmartRefreshHeaderConfig(R.drawable.refresh_head_arrow))
            .initScreenAutoSize()
    }

}
```

Koin新建 Module 对象


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
页面使用:
<br>
layout绑定ViewModel


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

ViewModel层接收数据并处理逻辑,最后通过LiveData通知View层展示页面;
<br>
TabViewModel:


```
    //koin inject
    val model by inject<AuthorModel>()

    //live data
    val tabDataCompleteLD by inject<SingleLiveData<Array<String>>>()

    val fragments = arrayListOf<Fragment>()

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

Model层获取数据：


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

View层展示数据：


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


