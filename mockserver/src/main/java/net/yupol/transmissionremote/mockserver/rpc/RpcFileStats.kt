package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcFileStats(
    @SerialName("bytesCompleted") val bytesCompleted: Long = 0,
    @SerialName("priority") val priority: Int = 0,
    @SerialName("wanted") val wanted: Boolean
)
