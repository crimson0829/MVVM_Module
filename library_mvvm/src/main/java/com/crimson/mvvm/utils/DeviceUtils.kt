@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package com.crimson.mvvm.utils

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.crimson.mvvm.ext.appContext
import com.crimson.mvvm.ext.equalsIgnoreCase
import com.crimson.mvvm.rx.sp.SPreferences
import com.crimson.mvvm.rx.sp.putStringSP
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * @author crimson
 * @date   2020-02-29
 * 设备工具类
 */
object DeviceUtils {
    /**
     * Return whether device is rooted.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDeviceRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
            "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
            "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
            "/system/sbin/", "/usr/bin/", "/vendor/bin/"
        )
        for (location in locations) {
            if (File(location + su).exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Return whether ADB is enabled.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isAdbEnabled(): Boolean {
        return Settings.Secure.getInt(
            appContext()?.contentResolver,
            Settings.Global.ADB_ENABLED, 0
        ) > 0
    }

    /**
     * Return the version name of device's system.
     *
     * @return the version name of device's system
     */
    fun getSDKVersionName(): String? {
        return Build.VERSION.RELEASE
    }

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    fun getSDKVersionCode(): Int {
        return Build.VERSION.SDK_INT
    }

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(): String {
        val id: String = Settings.Secure.getString(
            appContext()?.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return if ("9774d56d682e549c" == id) "" else id
    }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`,
     * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET, CHANGE_WIFI_STATE])
    fun getMacAddress(): String? {
        val macAddress = getMacAddress(*(null as Array<String?>?)!!)
        if (macAddress != "" || getWifiEnabled()) return macAddress
        setWifiEnabled(true)
        setWifiEnabled(false)
        return getMacAddress(*(null as Array<String?>?)!!)
    }

    @SuppressLint("WifiManagerLeak")
    private fun getWifiEnabled(): Boolean {
        val manager =
            appContext()?.getSystemService(WIFI_SERVICE) as WifiManager
        return manager.isWifiEnabled
    }

    /**
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    private fun setWifiEnabled(enabled: Boolean) {
        @SuppressLint("WifiManagerLeak") val manager =
            appContext()?.getSystemService(WIFI_SERVICE) as WifiManager
        if (enabled == manager.isWifiEnabled) return
        manager.isWifiEnabled = enabled
    }

    /**
     * Return the MAC address.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
    fun getMacAddress(vararg excepts: String?): String {
        var macAddress = getMacAddressByNetworkInterface()
        if (isAddressNotInExcepts(macAddress, *excepts as Array<out String>)) {
            return macAddress
        }
        macAddress = getMacAddressByInetAddress()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        macAddress = getMacAddressByWifiInfo()
        if (isAddressNotInExcepts(macAddress, *excepts)) {
            return macAddress
        }
        return if (isAddressNotInExcepts(macAddress, *excepts)) {
            macAddress
        } else ""
    }

    private fun isAddressNotInExcepts(
        address: String,
        vararg excepts: String
    ): Boolean {
        if (excepts.isEmpty()) {
            return "02:00:00:00:00:00" != address
        }
        for (filter in excepts) {
            if (address == filter) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getMacAddressByWifiInfo(): String {
        try {
            val wifi =
                appContext()?.applicationContext?.getSystemService(WIFI_SERVICE) as WifiManager
            val info = wifi.connectionInfo
            if (info != null) return info.macAddress
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByNetworkInterface(): String {
        try {
            val nis: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni: NetworkInterface = nis.nextElement()
                if (!ni.name.equalsIgnoreCase("wlan0")) continue
                val macBytes: ByteArray = ni.hardwareAddress
                if (macBytes.isNotEmpty()) {
                    val sb = StringBuilder()
                    for (b in macBytes) {
                        sb.append(String.format("%02x:", b))
                    }
                    return sb.substring(0, sb.length - 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getMacAddressByInetAddress(): String {
        try {
            val inetAddress: InetAddress? = getInetAddress()
            if (inetAddress != null) {
                val ni: NetworkInterface = NetworkInterface.getByInetAddress(inetAddress)
                val macBytes: ByteArray = ni.hardwareAddress
                if (macBytes.isNotEmpty()) {
                    val sb = StringBuilder()
                    for (b in macBytes) {
                        sb.append(String.format("%02x:", b))
                    }
                    return sb.substring(0, sb.length - 1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "02:00:00:00:00:00"
    }

    private fun getInetAddress(): InetAddress? {
        try {
            val nis: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni: NetworkInterface = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp) continue
                val addresses: Enumeration<InetAddress> = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress: InetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress: String = inetAddress.hostAddress
                        if (hostAddress.indexOf(':') < 0) return inetAddress
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Return the manufacturer of the product/hardware.
     *
     * e.g. Xiaomi
     *
     * @return the manufacturer of the product/hardware
     */
    fun getManufacturer(): String? {
        return Build.MANUFACTURER
    }

    /**
     * Return the model of device.
     *
     * e.g. MI2SC
     *
     * @return the model of device
     */
    fun getModel(): String? {
        var model = Build.MODEL
        model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return model
    }

    /**
     * Return an ordered list of ABIs supported by this device. The most preferred ABI is the first
     * element in the list.
     *
     * @return an ordered list of ABIs supported by this device
     */
    fun getABIs(): Array<String?>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
            } else arrayOf(Build.CPU_ABI)
        }
    }

    /**
     * Return whether device is tablet.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isTablet(): Boolean {
        return ((appContext()?.resources?.configuration?.screenLayout?.and(Configuration.SCREENLAYOUT_SIZE_MASK))!!
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    /**
     * Return whether device is emulator.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @SuppressLint("DefaultLocale")
    fun isEmulator(): Boolean {
        val checkProperty = (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
        if (checkProperty) return true
        var operatorName = ""
        val tm =
            appContext()?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val name = tm.networkOperatorName
        if (name != null) {
            operatorName = name
        }
        val checkOperatorName = operatorName.toLowerCase() == "android"
        if (checkOperatorName) return true
        val url = "tel:" + "123456"
        val intent = Intent()
        intent.data = Uri.parse(url)
        intent.action = Intent.ACTION_DIAL
        val checkDial =
            appContext()?.packageManager?.let { intent.resolveActivity(it) } == null
        return checkDial
        //        boolean checkDebuggerConnected = Debug.isDebuggerConnected();
//        if (checkDebuggerConnected) return true;
    }


    private const val KEY_UDID = "KEY_UDID"
    @Volatile
    private var udid: String? = null

    /**
     * Return the unique device id.
     * <pre>{1}{UUID(macAddress)}</pre>
     * <pre>{2}{UUID(androidId )}</pre>
     * <pre>{9}{UUID(random    )}</pre>
     *
     * @return the unique device id
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getUniqueDeviceId(): String? {
        return getUniqueDeviceId("")
    }

    /**
     * Return the unique device id.
     * <pre>android 10 deprecated {prefix}{1}{UUID(macAddress)}</pre>
     * <pre>{prefix}{2}{UUID(androidId )}</pre>
     * <pre>{prefix}{9}{UUID(random    )}</pre>
     *
     * @param prefix The prefix of the unique device id.
     * @return the unique device id
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getUniqueDeviceId(prefix: String): String? {
        if (udid == null) {
            synchronized(DeviceUtils::class.java) {
                if (udid == null) {
                    val id = SPreferences.get()!!.getString(KEY_UDID, "")
                    if (id != null) {
                        udid = id
                        return udid
                    }
                    try {
                        val androidId = getAndroidID()
                        if (!TextUtils.isEmpty(androidId)) {
                            return saveUdid(prefix + 2, androidId)
                        }
                    } catch (ignore: Exception) { /**/
                    }
                    return saveUdid(prefix + 9, "")
                }
            }
        }
        return udid
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun isSameDevice(uniqueDeviceId: String): Boolean { // {prefix}{type}{32id}
        if (TextUtils.isEmpty(uniqueDeviceId) && uniqueDeviceId.length < 33) return false
        if (uniqueDeviceId == udid) return true
        val cachedId: String? = SPreferences.get()!!.getString(KEY_UDID, null)
        if (uniqueDeviceId == cachedId) return true
        val st = uniqueDeviceId.length - 33
        val type = uniqueDeviceId.substring(st, st + 1)
        if (type.startsWith("2")) {
            val androidId = getAndroidID()
            return if (TextUtils.isEmpty(androidId)) {
                false
            } else uniqueDeviceId.substring(st + 1) == getUdid("", androidId)
        }
        return false
    }

    private fun saveUdid(prefix: String, id: String): String? {
        udid = getUdid(prefix, id)
        udid?.putStringSP(KEY_UDID)
        return udid
    }

    private fun getUdid(prefix: String, id: String): String {
        return if (id == "") {
            prefix + UUID.randomUUID().toString().replace("-", "")
        } else prefix + UUID.nameUUIDFromBytes(id.toByteArray()).toString().replace("-", "")
    }
}