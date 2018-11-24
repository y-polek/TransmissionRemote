package net.yupol.transmissionremote.data.api

import android.annotation.SuppressLint
import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.yupol.transmissionremote.data.api.rpc.RpcRequestBodyConverterFactory
import net.yupol.transmissionremote.domain.model.Server
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class Transport(private val server: Server, vararg interceptors: Interceptor) {

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Wrapped.ADAPTER_FACTORY)
            .add(FirstElement.ADAPTER_FACTORY)
            .build()

    @get:JvmName(name = "api")
    val api: TransmissionRpcApi by lazy {

        val okHttpClient = with(OkHttpClient.Builder()) {
            addInterceptor(SessionIdInterceptor())
            addInterceptor(RpcFailureInterceptor(moshi))
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            if (server.authEnabled()) {
                authenticator(BasicAuthenticator(server.login.orEmpty(), server.password.orEmpty()))
            }
            if (server.trustSelfSignedSslCert) {
                trustAllCertificates()
            }
            build()
        }

        val baseUrl = with (HttpUrl.Builder()) {
            scheme(if (server.https) "https" else "http")
            val port = server.port
            if (port != null) port(port)
            try {
                host(server.host)
            } catch (e: IllegalArgumentException) {
                // Catching exception to prevent crashes caused by invalid host name saved in previous versions of the app.
                // In later versions host name validation was added, so this exception should not be thrown.
                // Invalid host name is replaced with "invalid_host" string to allow normal processing.
                // After connection failure users are supposed to change host name to valid one in settings.
                host("invalid_host")
            }
            if (server.rpcPath.isNotEmpty()) {
                val path = server.rpcPath.removePrefix("/").removeSuffix("/")
                addPathSegments("$path/")
            }
            build()
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(RpcRequestBodyConverterFactory(moshi))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        retrofit.create(TransmissionRpcApi::class.java)
    }

    private fun OkHttpClient.Builder.trustAllCertificates() {

        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
        hostnameVerifier { _, _ -> true }
    }

}
