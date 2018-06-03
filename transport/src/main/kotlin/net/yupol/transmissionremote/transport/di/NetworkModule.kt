package net.yupol.transmissionremote.transport.di

import android.annotation.SuppressLint
import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.yupol.transmissionremote.model.Server
import net.yupol.transmissionremote.transport.*
import net.yupol.transmissionremote.transport.rpc.RpcRequestBodyConverterFactory
import okhttp3.HttpUrl
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

@Module
class NetworkModule(private val server: Server) {

    @Provides
    @ServerScope
    fun provideServer() = server

    @Provides
    @ServerScope
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(Wrapped.ADAPTER_FACTORY)
                .add(FirstElement.ADAPTER_FACTORY)
                .build()
    }

    @Provides
    @ServerScope
    fun provideOkHttpClient(
            server: Server,
            moshi: Moshi,
            connectivityInterceptor: ConnectivityInterceptor): OkHttpClient
    {
        return with(OkHttpClient.Builder()) {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            addInterceptor(SessionIdInterceptor())
            addInterceptor(RpcFailureInterceptor(moshi))
            addInterceptor(connectivityInterceptor)
            if (!server.userName.isNullOrEmpty()) {
                authenticator(BasicAuthenticator(server.userName, server.password))
            }
            if (server.trustSelfSignedSslCert) {
                trustAllCertificates()
            }
            build()
        }
    }

    @Provides
    @ServerScope
    fun provideRetrofit(server: Server, okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val baseUrl = with (HttpUrl.Builder()) {
            scheme(if (server.useHttps()) "https" else "http")
            if (server.port >= 0) port(server.port)
            try {
                host(server.host)
            } catch (e: IllegalArgumentException) {
                // Catching exception to prevent crashes caused by invalid host name saved in previous versions of the app.
                // In later versions host name validation was added, so this exception should not be thrown.
                // Invalid host name is replaced with "invalid_host" string to allow normal processing.
                // After connection failure users are supposed to change host name to valid one in settings.
                host("invalid_host")
            }
            if (server.urlPath.isNotEmpty()) {
                val path = server.urlPath.removePrefix("/").removeSuffix("/")
                addPathSegments("$path/")
            }
            build()
        }

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(RpcRequestBodyConverterFactory.create(moshi))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
    }

    @Provides
    @ServerScope
    fun providerRpcApi(retrofit: Retrofit): TransmissionRpcApi {
        return retrofit.create(TransmissionRpcApi::class.java)
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