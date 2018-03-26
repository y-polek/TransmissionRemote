package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import net.yupol.transmissionremote.model.Server
import net.yupol.transmissionremote.model.baseUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class Transport(private val server: Server) {

    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Wrapped.ADAPTER_FACTORY)
            .build()

    val api: TransmissionRpcApi by lazy {

        val okHttpClient = with(OkHttpClient.Builder()) {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            addInterceptor(SessionIdInterceptor())
            if (server.login.isNotEmpty()) {
                authenticator(BasicAuthenticator(server.login, server.password))
            }
            build()
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(server.baseUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        retrofit.create(TransmissionRpcApi::class.java)
    }
}