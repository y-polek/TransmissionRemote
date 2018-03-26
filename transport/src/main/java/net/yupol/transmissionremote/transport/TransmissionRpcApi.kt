package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.Wrapped
import io.reactivex.Single
import net.yupol.transmissionremote.model.ServerSettings
import net.yupol.transmissionremote.model.Torrent
import retrofit2.http.Body
import retrofit2.http.POST

interface TransmissionRpcApi {

    @POST("./")
    @Wrapped(path = ["arguments", "torrents"])
    fun torrentList(@Body body: RpcRequest = RpcRequest.torrentGet()): Single<List<Torrent>>

    @POST("./")
    @Wrapped(path = ["arguments"])
    fun serverSettings(@Body body: RpcRequest = RpcRequest.sessionGet()): Single<ServerSettings>
}