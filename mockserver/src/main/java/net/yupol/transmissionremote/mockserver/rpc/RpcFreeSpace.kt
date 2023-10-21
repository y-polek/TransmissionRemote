package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcFreeSpace(
    @SerialName("path") val path: String?,
    @SerialName("size-bytes") val size: Long
) : RpcArguments
