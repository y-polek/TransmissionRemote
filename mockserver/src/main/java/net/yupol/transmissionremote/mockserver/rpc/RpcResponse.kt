package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RpcResponse(
    @SerialName("arguments") val arguments: JsonElement,
    @SerialName("result") val result: String
)
