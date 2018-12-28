package net.yupol.transmissionremote.app.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.yupol.transmissionremote.app.preferences.Preferences
import net.yupol.transmissionremote.data.api.*
import net.yupol.transmissionremote.data.api.rpc.RpcRequestBodyConverterFactory
import net.yupol.transmissionremote.data.repository.ServerRepositoryImpl
import net.yupol.transmissionremote.data.repository.TorrentListRepositoryImpl
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.repository.ServerRepository
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import net.yupol.transmissionremote.domain.usecase.torrent.LoadTorrentList
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named

@Module
class ServerModule {

    @Provides
    @ServerScope
    fun provideOkHttpClient(
            server: Server,
            connectivityInterceptor: ConnectivityInterceptor,
            sessionIdInterceptor: SessionIdInterceptor,
            rpcFailureInterceptor: RpcFailureInterceptor): OkHttpClient
    {
        return with(OkHttpClient.Builder()) {
            addInterceptor(connectivityInterceptor)
            addInterceptor(sessionIdInterceptor)
            addInterceptor(rpcFailureInterceptor)
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            if (server.authEnabled()) {
                authenticator(BasicAuthenticator(server.login.orEmpty(), server.password.orEmpty()))
            }
            if (server.trustSelfSignedSslCert) {
                trustAllCertificates()
            }
            build()
        }
    }

    @Provides
    @ServerScope
    @Named("BASE_URL")
    fun provideBaseUrl(server: Server): HttpUrl {
        return with (HttpUrl.Builder()) {
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
    }

    @Provides
    @ServerScope
    fun provideRetrofit(
            @Named("BASE_URL") baseUrl: HttpUrl,
            okHttpClient: OkHttpClient,
            moshi: Moshi,
            rpcBodyConverterFactory: RpcRequestBodyConverterFactory): Retrofit
    {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(rpcBodyConverterFactory)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
    }

    @Provides
    @ServerScope
    fun provideTransmissionRpcApi(retrofit: Retrofit): TransmissionRpcApi = retrofit.create(TransmissionRpcApi::class.java)

    @Provides
    @ServerScope
    fun torrentListRepository(impl: TorrentListRepositoryImpl): TorrentListRepository = impl

    @Provides
    @ServerScope
    fun provideLoadTorrentListUseCase(repo: TorrentListRepository, preferences: Preferences): LoadTorrentList = LoadTorrentList(repo, preferences.updateInterval)

    @Provides
    @ServerScope
    fun serverRepository(impl: ServerRepositoryImpl): ServerRepository = impl
}
