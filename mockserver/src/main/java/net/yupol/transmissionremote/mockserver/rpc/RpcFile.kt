package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcFile(
    @SerialName("name") val name: String,
    @SerialName("length") val length: Long,
    @SerialName("bytesCompleted") val bytesCompleted: Long = 0
)
