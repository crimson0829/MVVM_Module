package com.crimson.mvvm.base

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.crimson.mvvm.ext.appContext
import com.crimson.mvvm.ext.application
import com.crimson.mvvm.ext.loge
import com.crimson.mvvm.ext.logw
import com.crimson.mvvm.livedata.SingleLiveData
import com.crimson.mvvm.rx.bus.RxBus
import com.trello.rxlifecycle3.LifecycleProvider
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.inject

/**
 * @author crimson
 * @date   2019-12-21
 * base view model
 * inject model in extends ViewModel
 */
open class BaseViewModel : AndroidViewModel(application()), IViewModel {


    /* inject rx bus*/
    val rxbus: RxBus by inject()

    /**
     * inject liveData
     */
    val onLoadingViewInjectToRootLD: SingleLiveData<Unit> by inject()

    val onLoadingViewResultLD: SingleLiveData<Unit> by inject()

    val dataLoadingLD: SingleLiveData<String> by inject()

    val dataResultLD: SingleLiveData<Unit> by inject()

    val dataLoadingErrorLD: SingleLiveData<Unit> by inject()

    val viewFinishedLD: SingleLiveData<Unit> by inject()

    /**
     * rx lifecycle
     * from view inject
     * now is not used
     * */
    var rxlifecycle: LifecycleProvider<*>? = null

    /**
     * lifecycle Owner
     */
    lateinit var lifecycleOwner: LifecycleOwner


    /*lifecycle call back*/
    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
        logw("onAny owner->$owner event->$event")
        lifecycleOwner = owner
    }

    override fun onCreate() {
    }

    override fun onStart() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onDestroy() {}


    /**
     *
     * 获取context，如果不是activity或者fragment 上下文 那就返回全局上下文
     *
     * @return
     */
    override fun context(): Context? {

        return when (lifecycleOwner) {
            is RxAppCompatActivity -> {
                lifecycleOwner as? Context
            }
            is RxFragment -> {
                (lifecycleOwner as? RxFragment)?.context
            }
            else -> {
                appContext()
            }
        }

    }


    /**
     * start a new activity
     */
    fun startActivity(clazz: Class<*>) {
        context()?.let {
            ContextCompat.startActivity(it, Intent(it, clazz), null)
        }

    }

    fun startActivity(intent: Intent) {
        context()?.let {
            ContextCompat.startActivity(it, intent, null)

        }
    }


    /**
     * process coroutine
     */
    fun launchCoroutine(block: suspend CoroutineScope.() -> Unit) =
        GlobalScope.launch(Dispatchers.Main) {
            try {
                block()
            } catch (e: Exception) {
                onLoadingError()
                loge(e.message)
            }
        }


    override fun registerRxBus() {}

    override fun removeRxBus() {}

    /**
     * call view on create loading when get data
     */
    fun onLoadingViewInjectToRoot() {
        onLoadingViewInjectToRootLD.call()
    }

    /**
     *call view on create when get data finish
     */
    fun onLoadingViewResult() {
        onLoadingViewResultLD.call()
    }


    /**
     * data loading event to view
     */
    fun onDataLoading(message: String = "") {
        dataLoadingLD.postValue(message)
    }

    /**
     * data result event to view
     */
    fun onDataResult() {
        dataResultLD.call()
    }

    fun onLoadingError() {
        dataLoadingErrorLD.call()
    }

    /**
     * view finish event
     */
    fun onViewFinished() {
        viewFinishedLD.call()
    }


}

