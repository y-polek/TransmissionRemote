package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcTorrents(
    @SerialName("torrents") var torrents: List<RpcTorrent>
) : RpcArguments
