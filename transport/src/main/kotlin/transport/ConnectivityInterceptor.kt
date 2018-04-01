package transport

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import android.net.NetworkInfo
import android.net.ConnectivityManager


class ConnectivityInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoNetworkException()
        }
        return chain.proceed(chain.request())
    }

    private fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

        return activeNetwork?.isConnectedOrConnecting ?: false
    }
}