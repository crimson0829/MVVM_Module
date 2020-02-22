package com.crimson.mvvm.rx.retry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Looper
import com.crimson.mvvm.ext.isNetConnected
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Action

/**
 * @author crimson
 * @date 2019/7/19
 * rx 广播订阅者.判断有无网络
 */
class BroadcastFlowable(private val context: Context) :
    FlowableOnSubscribe<Boolean?> {

    companion object {
        @JvmStatic
        fun fromConnectivityManager(context: Context): Flowable<Boolean> {
            return Flowable.create<Boolean>(
                BroadcastFlowable(context),
                BackpressureStrategy.LATEST
            ).share()
        }

        private fun unsubscribeInUiThread(action: Action): Disposable {
            return Disposables.fromAction {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    action.run()
                } else {
                    val inner = AndroidSchedulers.mainThread().createWorker()
                    inner.schedule {
                        try {
                            action.run()
                            inner.dispose()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    private val isConnectedToInternet: Boolean
        get() {
            return isNetConnected()
        }

    @Suppress("DEPRECATION")
    override fun subscribe(emitter: FlowableEmitter<Boolean?>) {
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                emitter.onNext(isConnectedToInternet)
            }
        }
        context.registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        emitter.setCancellable {
            unsubscribeInUiThread(
                Action { context.unregisterReceiver(receiver) }
            )
        }
    }


}