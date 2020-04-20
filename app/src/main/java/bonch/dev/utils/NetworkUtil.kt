package bonch.dev.utils

import android.content.Context
import android.net.ConnectivityManager
import bonch.dev.data.network.passanger.signup.NetworkService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkUtil {
    private const val BASE_URL = "https://poputi-backend-staging.server.bonch.dev"

    fun makeRetrofitService(): NetworkService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(NetworkService::class.java)
    }


    fun isNetworkConnected(context: Context): Boolean {
        val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mActiveNetwork = mConnectivityManager.activeNetworkInfo
        return mActiveNetwork != null && mActiveNetwork.isConnectedOrConnecting
    }
}