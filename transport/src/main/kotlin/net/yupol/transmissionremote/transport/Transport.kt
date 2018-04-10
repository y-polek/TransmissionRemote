package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import net.yupol.transmissionremote.model.Server
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import net.yupol.transmissionremote.transport.rpc.RpcRequestBodyConverterFactory
import java.util.*

class Transport(private val server: Server, vararg interceptors: Interceptor) {

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Wrapped.ADAPTER_FACTORY)
            .build()

    @get:JvmName(name = "api")
    val api: TransmissionRpcApi by lazy {

        val okHttpClient = with(OkHttpClient.Builder()) {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            addInterceptor(SessionIdInterceptor())
            addInterceptor(RpcFailureInterceptor(moshi))
            for (interceptor in interceptors) {
                addInterceptor(interceptor)
            }
            if (!server.userName.isNullOrEmpty()) {
                authenticator(BasicAuthenticator(server.userName, server.password))
            }
            build()
        }

        val baseUrl = String.format(Locale.ROOT, "%s://%s:%d/%s/",
                if (server.useHttps()) "https" else "http",
                server.host,
                server.port,
                server.urlPath)

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(RpcRequestBodyConverterFactory.create(moshi))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        retrofit.create(TransmissionRpcApi::class.java)
    }
}