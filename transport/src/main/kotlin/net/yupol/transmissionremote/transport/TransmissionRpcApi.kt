package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.Wrapped
import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.model.json.ServerSettings
import net.yupol.transmissionremote.model.json.Torrent
import retrofit2.http.*
import net.yupol.transmissionremote.transport.rpc.RpcArgs
import net.yupol.transmissionremote.transport.rpc.RpcIds
import net.yupol.transmissionremote.transport.rpc.RpcMethod

interface TransmissionRpcApi {

    @POST("./")
    @RpcMethod("torrent-get")
    @Wrapped(path = ["arguments", "torrents"])
    fun torrentList(@Body body: Map<String, @JvmSuppressWildcards Any> = RpcArgs.torrentGet()): Single<List<Torrent>>

    @POST("./")
    @RpcMethod("session-get")
    @Wrapped(path = ["arguments"])
    fun serverSettings(@Body body: Map<String, @JvmSuppressWildcards Any> = RpcArgs.sessionGet()): Single<ServerSettings>

    @POST("./")
    @RpcMethod("session-set")
    fun setServerSettings(@Body body: Map<String, @JvmSuppressWildcards Any>): Completable

    @POST(".")
    @RpcMethod("torrent-start")
    fun startTorrents(@RpcIds @Body vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-start-now")
    fun startTorrentsNoQueue(@RpcIds @Body vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-stop")
    fun stopTorrents(@RpcIds @Body vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-reannounce")
    fun reannounceTorrents(@RpcIds @Body vararg ids: Int): Completable

    @POST(".")
    @RpcMethod("torrent-verify")
    fun verifyTorrents(@RpcIds @Body vararg ids: Int): Completable
}