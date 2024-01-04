package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcPeer(
    @SerialName("address") val address: String,
    @SerialName("port") val port: Int,
    @SerialName("clientName") val clientName: String,
    @SerialName("isEncrypted") val isEncrypted: Boolean,
    @SerialName("progress") val progress: Double,
    @SerialName("rateToClient") val rateToClient: Long,
    @SerialName("rateToPeer") val rateToPeer: Long
)
