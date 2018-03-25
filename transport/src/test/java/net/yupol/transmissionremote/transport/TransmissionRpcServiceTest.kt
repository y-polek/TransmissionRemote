package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class TransmissionRpcServiceTest {

    private lateinit var rpcService: TransmissionRpcService

    @Before
    fun setup() {

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(SessionIdInterceptor())
                .authenticator(BasicAuthenticator("transmission", "transmission"))
                .build()

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(Wrapped.ADAPTER_FACTORY)
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://polek.ddns.net:9093")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        rpcService = retrofit.create(TransmissionRpcService::class.java)
    }

    @Test
    fun test() {

        rpcService.torrentList()
                .subscribe(
                        { torrents ->
                            println("Torrents: $torrents")
                        },
                        { error ->
                            println("Error: ${error.message}")
                        })
    }
}