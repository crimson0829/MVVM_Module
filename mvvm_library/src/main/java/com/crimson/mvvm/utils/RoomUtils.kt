package com.crimson.mvvm.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 * room工具类
 */
object RoomUtils {

    const val ROM_MIUI = "MIUI"
    const val ROM_EMUI = "EMUI"
    const val ROM_FLYME = "FLYME"
    const val ROM_OPPO = "OPPO"
    const val ROM_SMARTISAN = "SMARTISAN"
    const val ROM_VIVO = "VIVO"
    const val ROM_QIKU = "QIKU"
    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"
    private var sName: String? = null
    private var sVersion: String? = null
    val isEmui: Boolean
        get() = check(ROM_EMUI)

    val isMiui: Boolean
        get() = check(ROM_MIUI)

    val isVivo: Boolean
        get() = check(ROM_VIVO)

    val isOppo: Boolean
        get() = check(ROM_OPPO)

    val isFlyme: Boolean
        get() = check(ROM_FLYME)

    fun is360(): Boolean {
        return check(ROM_QIKU) || check(
            "360"
        )
    }

    val isSmartisan: Boolean
        get() = check(ROM_SMARTISAN)

    val name: String?
        get() {
            if (sName == null) {
                check("")
            }
            return sName
        }

    val version: String?
        get() {
            if (sVersion == null) {
                check("")
            }
            return sVersion
        }

    fun check(rom: String): Boolean {
        if (sName != null) {
            return sName == rom
        }
        if (!TextUtils.isEmpty(
                getProp(KEY_VERSION_MIUI).also { sVersion = it })
        ) {
            sName = ROM_MIUI
        } else if (!TextUtils.isEmpty(
                getProp(KEY_VERSION_EMUI).also { sVersion = it })
        ) {
            sName = ROM_EMUI
        } else if (!TextUtils.isEmpty(
                getProp(KEY_VERSION_OPPO).also { sVersion = it })
        ) {
            sName = ROM_OPPO
        } else if (!TextUtils.isEmpty(
                getProp(KEY_VERSION_VIVO).also { sVersion = it })
        ) {
            sName = ROM_VIVO
        } else if (!TextUtils.isEmpty(
                getProp(KEY_VERSION_SMARTISAN).also { sVersion = it })
        ) {
            sName = ROM_SMARTISAN
        } else {
            sVersion = Build.DISPLAY
            if (sVersion != null && sVersion!!.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME
            } else {
                sVersion = Build.UNKNOWN
                sName = Build.MANUFACTURER.toUpperCase()
            }
        }
        return sName == rom
    }

    fun getProp(name: String): String? {
        var line: String? = null
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $name")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) { //            Log.e(TAG, "Unable to read prop " + name, ex);
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    val systemLanguage: String
        get() = Locale.getDefault().language

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    val systemLanguageList: Array<Locale>
        get() = Locale.getAvailableLocales()

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    val systemVersion: String
        get() = Build.VERSION.RELEASE

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    val systemModel: String
        get() = Build.MODEL

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    val deviceBrand: String
        get() = Build.BRAND

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
                if (pn == "com.tencent.mm") {
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
                        "com.tencent.qqlite",
                        ignoreCase = true
                    ) || pn.equals("com.tencent.mobileqq", ignoreCase = true)
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
                if (pn == "com.sina.weibo") {
                    return true
                }
            }
        }
        return false
    }

}