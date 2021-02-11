
package com.sudoajay.historycachecleaner.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.sudoajay.historyprivacycleaner.R


object ConnectivityType {

    fun getNetworkProvider(context: Context): String {
        var result = ""
//        val connectivityManager: ConnectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val networkCapabilities = connectivityManager.activeNetwork ?: return context.getString(
//                R.string.no_internet_text
//            )
//            val actNw =
//                connectivityManager.getNetworkCapabilities(networkCapabilities)
//                    ?: return context.getString(R.string.no_internet_text)
//            result = when {
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> context.getString(R.string.wifi_text)
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> context.getString(R.string.mobile_data_text)
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> context.getString(R.string.vpn_text)
//                else -> context.getString(R.string.no_internet_text)
//            }
//        } else {
//            connectivityManager.run {
//                connectivityManager.activeNetworkInfo?.run {
//                    result = when (type) {
//                        ConnectivityManager.TYPE_WIFI -> context.getString(R.string.wifi_text)
//                        ConnectivityManager.TYPE_MOBILE -> context.getString(R.string.mobile_data_text)
//                        ConnectivityManager.TYPE_VPN -> context.getString(R.string.vpn_text)
//                        else -> context.getString(R.string.no_internet_text)
//                    }
//
//                }
//            }
//        }

        return result
    }

}
