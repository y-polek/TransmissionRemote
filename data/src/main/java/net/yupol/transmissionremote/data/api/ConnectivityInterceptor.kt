package net.yupol.transmissionremote.data.api

import net.yupol.transmissionremote.device.connectivity.Connectivity
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityInterceptor @Inject constructor(private val connectivity: Connectivity) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivity.isConnected()) {
            throw NoNetworkException(connectivity.isAirplaneModeOn())
        }
        return chain.proceed(chain.request())
    }
}
