package net.yupol.transmissionremote.transport

import com.serjltt.moshi.adapters.Wrapped
import io.reactivex.Single
import net.yupol.transmissionremote.model.Torrent
import retrofit2.http.Body
import retrofit2.http.POST

interface TransmissionRpcService {

    @POST("/transmission/rpc")
    @Wrapped(path = ["arguments", "torrents"])
    fun torrentList(@Body body: RpcRequest = RpcRequest.torrentGet()): Single<List<Torrent>>
}