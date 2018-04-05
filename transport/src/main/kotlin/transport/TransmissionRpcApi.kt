package transport

import com.serjltt.moshi.adapters.Wrapped
import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.model.json.ServerSettings
import net.yupol.transmissionremote.model.json.Torrent
import retrofit2.http.*
import transport.rpc.RpcMethod

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
    @Wrapped(path = ["result"])
    fun setServerSettings(@Body body: RpcArgs): Completable

    @POST("./")
    fun action(@Body body: RpcArgs): Completable

    @POST(".")
    @RpcMethod("torrent-start")
    fun startTorrent(@Body body: RpcArgs): Completable
}