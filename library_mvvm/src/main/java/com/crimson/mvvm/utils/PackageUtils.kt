@file:Suppress("DEPRECATION")

package com.crimson.mvvm.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.crimson.mvvm.ext.tryCatch
import com.crimson.mvvm.ext.view.toast

/**
 * @author crimson
 * @date   2020-02-29
 *
 */
object PackageUtils {


    //淘宝
    const val TAOBAO_PKG = "com.taobao.taobao"
    //天猫
    const val TIANMAO_PKG = "com.tmall.wireless"
    //京东
    const val JINGDONG_PKG = "com.jingdong.app.mall"
    //拼多多
    const val PINDUODUO_PKG = "com.xunmeng.pinduoduo"
    //微信
    const val WECHAT_PKG = "com.tencent.mm"
    //微博
    const val SINA_PKG = "com.sina.weibo"
    //qq
    const val QQ_PKG = "com.tencent.mobileqq"
    const val QQ_LITE_PKG = "com.tencent.qqlite"

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context context
     * @param pkgName 应用包名
     * @return true:已安装；false：未安装
     */
    fun isAPPInstalled(
        context: Context?,
        pkgName: String? = ""
    ): Boolean {
        var packageInfo: PackageInfo?
        try {
            packageInfo = context?.packageManager?.getPackageInfo(pkgName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    fun isAliPayInstall(context: Context): Boolean {
        val uri = Uri.parse("alipays://platformapi/startApp")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val componentName = intent.resolveActivity(context.packageManager)
        return componentName != null
    }

    /**
     * 判断 用户是否安装微信客户端
     */
    fun isWeChatInstall(context: Context): Boolean {
        val packageManager = context.packageManager // 获取packagemanager
        val pinfo =
            packageManager.getInstalledPackages(0) // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == WECHAT_PKG) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    fun isQQInstall(context: Context): Boolean {
        val packageManager = context.packageManager
        val pinfo =
            packageManager.getInstalledPackages(0)
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn.equals(
                        QQ_LITE_PKG,
                        ignoreCase = true
                    ) || pn.equals(QQ_PKG, ignoreCase = true)
                ) {
                    return true
                }
            }
        }
        return false
    }


    /**
     * sina
     * 判断是否安装新浪微博
     */
    fun isSinaInstall(context: Context): Boolean {
        val packageManager = context.packageManager // 获取packagemanager
        val pinfo =
            packageManager.getInstalledPackages(0) // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn == SINA_PKG) {
                    return true
                }
            }
        }
        return false
    }


    /**
     * 根据包名打开app
     */
    fun openApp(context: Context?, pkgName: String) {
        tryCatch {
            if (isAPPInstalled(context, pkgName)) {
                context?.packageManager?.getLaunchIntentForPackage(pkgName)?.apply {
                    context.startActivity(this)
                }
            } else {
                toast("请先安装App")
            }
        }

    }

    /**
     * 根据路径 path 跳转具体页面
     *
     */
    fun openPageWithPath(context: Context, pkgName: String, path: String) {
        tryCatch {
            if (isAPPInstalled(context, pkgName)) {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse(path)
                context.startActivity(intent)
            } else {
                toast("请先安装App")
            }
        }

    }


}