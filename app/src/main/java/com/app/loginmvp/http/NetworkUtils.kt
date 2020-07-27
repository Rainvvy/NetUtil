package com.app.loginmvp.http

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import android.util.Log
import com.app.loginmvp.MyApp
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException

/**

 * Create by wy on 2019/10/25 09:10

 */
class NetworkUtils {
    private fun NetworkUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    enum class NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }
    companion object{
        /**
         * Open the settings of wireless.
         */
        fun openWirelessSettings() {
            MyApp.myApp?.startActivity(
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        /**
         * Return whether network is connected.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: connected<br></br>`false`: disconnected
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isConnected(): Boolean {
            val info = getActiveNetworkInfo()
            return info != null && info.isConnected
        }

        /**
         * Return whether network is available using ping.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * The default ping ip: 223.5.5.5
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPing(): Boolean {
            return isAvailableByPing(null)
        }

        /**
         * Return whether network is available using ping.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param ip The ip address.
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPing(ip: String?): Boolean {
            var ip = ip
            if (ip == null || ip.length <= 0) {
                ip = "223.5.5.5"// default ping ip
            }
            val result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false)
            val ret = result.result === 0
            if (result.errorMsg != null) {
                Log.d("NetworkUtils", "isAvailableByPing() called" + result.errorMsg)
            }
            if (result.successMsg != null) {
                Log.d("NetworkUtils", "isAvailableByPing() called" + result.successMsg)
            }
            return ret
        }

        /**
         * Return whether mobile data is enabled.
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        fun getMobileDataEnabled(): Boolean {
            try {
                val tm = MyApp.myApp?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager ?: return false
                @SuppressLint("PrivateApi")
                val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
                if (null != getMobileDataEnabledMethod) {
                    return getMobileDataEnabledMethod.invoke(tm) as Boolean
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

        /**
         * Set mobile data enabled.
         *
         * Must hold `android:sharedUserId="android.uid.system"`,
         * `<uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />`
         *
         * @param enabled True to enabled, false otherwise.
         */
        @RequiresPermission(MODIFY_PHONE_STATE)
        fun setMobileDataEnabled(enabled: Boolean) {
            try {
                val tm = MyApp.myApp?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager ?: return
                val setMobileDataEnabledMethod =
                    tm.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
                setMobileDataEnabledMethod?.invoke(tm, enabled)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * Return whether using mobile data.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isMobileData(): Boolean {
            val info = getActiveNetworkInfo()
            return (null != info
                    && info.isAvailable
                    && info.type == ConnectivityManager.TYPE_MOBILE)
        }

        /**
         * Return whether using 4G.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun is4G(): Boolean {
            val info = getActiveNetworkInfo()
            return (info != null
                    && info.isAvailable
                    && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
        }

        /**
         * Return whether wifi is enabled.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getWifiEnabled(): Boolean {
            @SuppressLint("WifiManagerLeak")
            val manager = MyApp.myApp?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager != null && manager.isWifiEnabled
        }

        /**
         * Set wifi enabled.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
         *
         * @param enabled True to enabled, false otherwise.
         */
        @SuppressLint("MissingPermission")
        fun setWifiEnabled(enabled: Boolean) {
            @SuppressLint("WifiManagerLeak")
            val manager = MyApp.myApp?.getSystemService(Context.WIFI_SERVICE) as WifiManager ?: return
            if (enabled) {
                if (!manager.isWifiEnabled) {
                    manager.isWifiEnabled = true
                }
            } else {
                if (manager.isWifiEnabled) {
                    manager.isWifiEnabled = false
                }
            }
        }

        /**
         * Return whether wifi is connected.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: connected<br></br>`false`: disconnected
         */
        @SuppressLint("MissingPermission")
        fun isWifiConnected(): Boolean {
            val cm = MyApp.myApp?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return (cm != null
                    && cm.activeNetworkInfo != null
                    && cm.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI)
        }

        /**
         * Return whether wifi is available.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: available<br></br>`false`: unavailable
         */
        @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
        fun isWifiAvailable(): Boolean {
            return getWifiEnabled() && isAvailableByPing()
        }

        /**
         * Return the name of network operate.
         *
         * @return the name of network operate
         */
        fun getNetworkOperatorName(): String? {
            val tm =MyApp.myApp?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm?.networkOperatorName
        }

        private val NETWORK_TYPE_GSM = 16
        private val NETWORK_TYPE_TD_SCDMA = 17
        private val NETWORK_TYPE_IWLAN = 18

        /**
         * Return type of network.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return type of network
         *
         *  * [NetworkType.NETWORK_WIFI]
         *  * [NetworkType.NETWORK_4G]
         *  * [NetworkType.NETWORK_3G]
         *  * [NetworkType.NETWORK_2G]
         *  * [NetworkType.NETWORK_UNKNOWN]
         *  * [NetworkType.NETWORK_NO]
         *
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun getNetworkType(): NetworkType {
            var netType = NetworkType.NETWORK_NO
            val info = getActiveNetworkInfo()
            if (info != null && info.isAvailable) {

                if (info.type == ConnectivityManager.TYPE_WIFI) {
                    netType = NetworkType.NETWORK_WIFI
                } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    when (info.subtype) {

                        NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> netType =
                            NetworkType.NETWORK_2G

                        NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> netType =
                            NetworkType.NETWORK_3G

                        NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> netType = NetworkType.NETWORK_4G
                        else -> {

                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                netType = NetworkType.NETWORK_3G
                            } else {
                                netType = NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                } else {
                    netType = NetworkType.NETWORK_UNKNOWN
                }
            }
            return netType
        }

        @RequiresPermission(ACCESS_NETWORK_STATE)
        private fun getActiveNetworkInfo(): NetworkInfo? {
            val manager =
                MyApp.myApp?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ?: return null
            return manager.activeNetworkInfo
        }

        /**
         * Return the ip address.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param useIPv4 True to use ipv4, false otherwise.
         * @return the ip address
         */
        @RequiresPermission(INTERNET)
        fun getIPAddress(useIPv4: Boolean): String? {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val inetAddress = addresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val hostAddress = inetAddress.hostAddress
                            val isIPv4 = hostAddress.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4) return hostAddress
                            } else {
                                if (!isIPv4) {
                                    val index = hostAddress.indexOf('%')
                                    return if (index < 0)
                                        hostAddress.toUpperCase()
                                    else
                                        hostAddress.substring(0, index).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * Return the domain address.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param domain The name of domain.
         * @return the domain address
         */
        @RequiresPermission(INTERNET)
        fun getDomainAddress(domain: String): String? {
            val inetAddress: InetAddress
            try {
                inetAddress = InetAddress.getByName(domain)
                return inetAddress.hostAddress
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                return null
            }

        }
    }

}