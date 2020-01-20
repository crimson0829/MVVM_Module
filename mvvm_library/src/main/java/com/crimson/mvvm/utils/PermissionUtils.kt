package com.crimson.mvvm.utils

import android.Manifest
import androidx.annotation.StringDef
import androidx.fragment.app.FragmentActivity
import com.crimson.mvvm.config.ViewLifeCycleManager
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * @author crimson
 * @date 2019/1/28
 * rx permission 工具类
 */
class PermissionUtils private constructor() {

    @StringDef(
        ACCESS_COARSE_LOCATION,
        ACCESS_FINE_LOCATION,
        READ_PHONE_STATE,
        WRITE_EXTERNAL_STORAGE,
        READ_EXTERNAL_STORAGE,
        CAMERA,
        READ_CALENDAR,
        WRITE_CALENDAR,
        WRITE_CONTACTS,
        READ_CONTACTS,
        RECORD_AUDIO,
        READ_SMS,
        CALL_PHONE
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ManifestPermission

    companion object {
        //目前需要动态获取的权限是这几个,如果想在其他的地方调不同的权限，需添加
        //定位
        const val ACCESS_COARSE_LOCATION =
            Manifest.permission.ACCESS_COARSE_LOCATION
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        //手机状态
        const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        //读写外部存储
        const val WRITE_EXTERNAL_STORAGE =
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val READ_EXTERNAL_STORAGE =
            Manifest.permission.READ_EXTERNAL_STORAGE
        //相机
        const val CAMERA = Manifest.permission.CAMERA
        //读写日历
        const val READ_CALENDAR = Manifest.permission.READ_CALENDAR
        const val WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR
        //读写联系人
        const val WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS
        const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
        //录音
        const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        //读取短信
        const val READ_SMS = Manifest.permission.READ_SMS
        //拨打电话
        const val CALL_PHONE = Manifest.permission.CALL_PHONE

        /**
         *
         * 检查权限，只返回全部通过为true，其他为false
         * @param permission
         * @return
         */
        fun checkPermission(
            @ManifestPermission vararg permission: String?,
            activity: FragmentActivity? = ViewLifeCycleManager.obtainCurrentActivity() as? FragmentActivity
        ): Observable<Boolean>? {
            if (activity == null) {
                return null
            }
            return RxPermissions(activity)
                .request(*permission)
                .observeOn(AndroidSchedulers.mainThread())
        }


        /**
         *
         * 逐个检查权限，依次返回
         * @param permission
         * @return
         */
        fun checkEachPermission(
            @ManifestPermission vararg permission: String?,
            activity: FragmentActivity? = ViewLifeCycleManager.obtainCurrentActivity() as? FragmentActivity
            ): Observable<Permission>? {
            if (activity == null) {
                return null
            }
            return RxPermissions(activity)
                .requestEach(*permission)
                .observeOn(AndroidSchedulers.mainThread())
        }


        /**
         * rx Transformer中check，相当于request
         * @param permission
         * @return
         */
        fun composePermission(
            @ManifestPermission vararg permission: String?,
            activity: FragmentActivity? = ViewLifeCycleManager.obtainCurrentActivity() as? FragmentActivity
            ): ObservableTransformer<Any, Permission>? {
            if (activity == null) {
                return null
            }
            return RxPermissions(activity)
                .ensureEach(*permission)
        }

    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}