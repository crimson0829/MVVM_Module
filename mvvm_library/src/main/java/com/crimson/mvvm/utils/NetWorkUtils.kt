@file:Suppress("DEPRECATION")

package com.crimson.mvvm.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

/**
 *
 * 网络工具类
 */
object NetWorkUtils {
    /**
     * 判断是否有网络连接
     *
     * @return
     */
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                val ni = cm.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val n = cm.activeNetwork

                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    return if (nc == null){
                        false
                    } else {
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                            NetworkCapabilities.TRANSPORT_WIFI)
                    }
                }
            }
        }
        return false

    }

    /**
     * 判断WIFI网络是否可用
     *
     * @return
     */
    fun isWifiConnected(context: Context): Boolean {
        val mconnectivityManagerManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wiFiNetworkInfo = mconnectivityManagerManager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wiFiNetworkInfo != null && wiFiNetworkInfo.isConnected
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @return
     */
    fun isMobileConnected(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobileNetworkInfo = connectivityManager
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return mobileNetworkInfo != null && mobileNetworkInfo.isConnected
    }

    /**
     * 获取当前网络连接的类型信息
     *
     * @return
     */
    fun getConnectedType(context: Context): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1
                    }
                }
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = 2
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = 1
                    }
                }
            }
        }

        return result
    }

    /***
     * 判断Network具体类型（联通移动wap，电信wap，其他net）
     *
     */
    fun checkNetworkType(context: Context): Int {
        try {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobNetInfoActivity = connectivityManager
                .activeNetworkInfo
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable) { // 注意一：
// NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
// 但是有些电信机器，仍可以正常联网，
// 所以当成net网络处理依然尝试连接网络。
// （然后在socket中捕捉异常，进行二次判断与用户提示）。
                return TYPE_NET_WORK_DISABLED
            } else { // NetworkInfo不为null开始判断是网络类型
                val netType = mobNetInfoActivity.type
                if (netType == ConnectivityManager.TYPE_WIFI) { // wifi net处理
                    return TYPE_WIFI
                } else if (netType == ConnectivityManager.TYPE_MOBILE) { // 注意二：
// 判断是否电信wap:
// 不要通过getExtraInfo获取接入点名称来判断类型，
// 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
// 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
// 所以可以通过这个进行判断！
                    val is4G = is4GMobileNetwork(context)
                    if (is4G) {
                        return TYPE_4G
                    }
                    val is3G = is3GMobileNetwork(context)
                    val c = context.contentResolver.query(
                        PREFERRED_APN_URI, null, null, null, null
                    )
                    if (c != null) {
                        c.moveToFirst()
                        val user = c.getString(
                            c
                                .getColumnIndex("user")
                        )
                        if (!TextUtils.isEmpty(user)) {
                            if (user.startsWith(CTWAP)) {
                                return if (is3G) TYPE_CT_WAP else TYPE_CT_WAP_2G
                            } else if (user.startsWith(CTNET)) {
                                return if (is3G) TYPE_CT_NET else TYPE_CT_NET_2G
                            }
                        }
                    }
                    c!!.close()
                    // 注意三：
// 判断是移动联通wap:
// 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
// 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
// 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
// 所以采用getExtraInfo获取接入点名字进行判断
                    var netMode = mobNetInfoActivity.extraInfo
                    //                    Logger.w("==================netmode:" + netMode);
                    if (netMode != null) { // 通过apn名称判断是否是联通和移动wap
                        netMode = netMode.toLowerCase()
                        if (netMode == CMWAP) {
                            return if (is3G) TYPE_CM_WAP else TYPE_CM_WAP_2G
                        } else if (netMode == CMNET) {
                            return if (is3G) TYPE_CM_NET else TYPE_CM_NET_2G
                        } else if (netMode == NET_3G || netMode == UNINET) {
                            return if (is3G) TYPE_CU_NET else TYPE_CU_NET_2G
                        } else if (netMode == WAP_3G || netMode == UNIWAP) {
                            return if (is3G) TYPE_CU_WAP else TYPE_CU_WAP_2G
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return TYPE_OTHER
        }
        return TYPE_OTHER
    }

    const val CTWAP = "ctwap"
    const val CTNET = "ctnet"
    const val CMWAP = "cmwap"
    const val CMNET = "cmnet"
    const val NET_3G = "3gnet"
    const val WAP_3G = "3gwap"
    const val UNIWAP = "uniwap"
    const val UNINET = "uninet"
    const val TYPE_CT_WAP = 5
    const val TYPE_CT_NET = 6
    const val TYPE_CT_WAP_2G = 7
    const val TYPE_CT_NET_2G = 8
    const val TYPE_CM_WAP = 9
    const val TYPE_CM_NET = 10
    const val TYPE_CM_WAP_2G = 11
    const val TYPE_CM_NET_2G = 12
    const val TYPE_CU_WAP = 13
    const val TYPE_CU_NET = 14
    const val TYPE_CU_WAP_2G = 15
    const val TYPE_CU_NET_2G = 16
    const val TYPE_OTHER = 17
    var PREFERRED_APN_URI = Uri
        .parse("content://telephony/carriers/preferapn")
    /**
     * 没有网络
     */
    const val TYPE_NET_WORK_DISABLED = 0
    /**
     * wifi网络
     */
    const val TYPE_WIFI = 4
    const val TYPE_4G = 3
    private fun is3GMobileNetwork(context: Context): Boolean {
        val telephonyManager = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> false // ~ 50-100 kbps
            TelephonyManager.NETWORK_TYPE_CDMA -> false // ~ 14-64 kbps
            TelephonyManager.NETWORK_TYPE_EDGE -> false // ~ 50-100 kbps
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> true // ~ 400-1000 kbps
            TelephonyManager.NETWORK_TYPE_EVDO_A -> true // ~ 600-1400 kbps
            TelephonyManager.NETWORK_TYPE_GPRS -> false // ~ 100 kbps
            TelephonyManager.NETWORK_TYPE_HSDPA -> true // ~ 2-14 Mbps
            TelephonyManager.NETWORK_TYPE_HSPA -> true // ~ 700-1700 kbps
            TelephonyManager.NETWORK_TYPE_HSUPA -> true // ~ 1-23 Mbps
            TelephonyManager.NETWORK_TYPE_UMTS -> true // ~ 400-7000 kbps
            TelephonyManager.NETWORK_TYPE_EHRPD -> true // ~ 1-2 Mbps
            TelephonyManager.NETWORK_TYPE_EVDO_B -> true // ~ 5 Mbps
            TelephonyManager.NETWORK_TYPE_HSPAP -> true // ~ 10-20 Mbps
            TelephonyManager.NETWORK_TYPE_IDEN -> false // ~25 kbps
            TelephonyManager.NETWORK_TYPE_LTE -> false // ~ 10+ Mbps
            TelephonyManager.NETWORK_TYPE_UNKNOWN -> false
            else -> false
        }
    }

    private fun is4GMobileNetwork(context: Context): Boolean {
        val telephonyManager = context
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_LTE -> true // ~ 10+ Mbps
            else -> false
        }
    }

    /**
     * 获取网络连接状态
     *
     * @return
     */
    fun getNetWorkState(context: Context): String? {
        val checkNetworkType = checkNetworkType(context)
        var state: String? = null
        when (checkNetworkType) {
            TYPE_WIFI -> state = "wifi"
            TYPE_4G -> state = "4G"
            TYPE_NET_WORK_DISABLED -> state = "no network"
            TYPE_CT_WAP -> state = "ctwap"
            TYPE_CT_WAP_2G -> state = "ctwap_2g"
            TYPE_CT_NET -> state = "ctnet"
            TYPE_CT_NET_2G -> state = "ctnet_2g"
            TYPE_CM_WAP -> state = "cmwap"
            TYPE_CM_WAP_2G -> state = "cmwap_2g"
            TYPE_CM_NET -> state = "cmnet"
            TYPE_CM_NET_2G -> state = "cmnet_2g"
            TYPE_CU_NET -> state = "cunet"
            TYPE_CU_NET_2G -> state = "cunet_2g"
            TYPE_CU_WAP -> state = "cuwap"
            TYPE_CU_WAP_2G -> state = "cuwap_2g"
            TYPE_OTHER -> state = "other"
            else -> {
            }
        }
        return state
    }

    fun getIPAddress(context: Context): String? {
        val info = (context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (info != null && info.isConnected) {
            if (info.type == ConnectivityManager.TYPE_MOBILE) { //当前使用2G/3G/4G网络
                try { //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    val en =
                        NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val intf = en.nextElement()
                        val enumIpAddr =
                            intf.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.getHostAddress()
                            }
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) { //当前使用无线网络
                val wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                return intIP2StringIP(wifiInfo.ipAddress)
            }
        } else { //当前无网络连接,请在设置中打开网络
        }
        return null
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    fun intIP2StringIP(ip: Int): String {
        return (ip and 0xFF).toString() + "." +
                (ip shr 8 and 0xFF) + "." +
                (ip shr 16 and 0xFF) + "." +
                (ip shr 24 and 0xFF)
    }

    /**
     * 获取当前的运营商
     *
     * @param context
     * @return 运营商名字
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getOperator(context: Context): String {
        var ProvidersName = ""
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val IMSI = telephonyManager.subscriberId
        Log.i("qweqwes", "运营商代码$IMSI")
        return if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                ProvidersName = "中国移动"
            } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
                ProvidersName = "中国联通"
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信"
            }
            ProvidersName
        } else {
            "未获取到sim卡信息"
        }
    }
}