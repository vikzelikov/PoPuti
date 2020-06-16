package bonch.dev.domain.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {

    fun isNetworkConnected(context: Context): Boolean {
        val mConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mActiveNetwork = mConnectivityManager.activeNetworkInfo
        return mActiveNetwork != null && mActiveNetwork.isConnectedOrConnecting
    }

    fun getHeaders(token: String): HashMap<String, String> {
        val headers = hashMapOf<String, String>()
        headers["Authorization"] = "Bearer $token"
        return headers
    }
}