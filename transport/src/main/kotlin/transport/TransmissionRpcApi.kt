package transport

import com.serjltt.moshi.adapters.Wrapped
import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.model.json.ServerSettings
import net.yupol.transmissionremote.model.json.Torrent
import retrofit2.http.Body
import retrofit2.http.POST

interface TransmissionRpcApi {

    @POST("./")
    @Wrapped(path = ["arguments", "torrents"])
    fun torrentList(@Body body: RpcRequest = RpcRequest.torrentGet()): Single<List<Torrent>>

    @POST("./")
    @Wrapped(path = ["arguments"])
    fun serverSettings(@Body body: RpcRequest = RpcRequest.sessionGet()): Single<ServerSettings>

    @POST("./")
    @Wrapped(path = ["result"])
    fun setServerSettings(@Body body: RpcRequest): Completable

    @POST("./")
    fun action(@Body body: RpcRequest): Completable
}