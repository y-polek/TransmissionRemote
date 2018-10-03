package net.yupol.transmissionremote.data.api.rpc

import com.squareup.moshi.Json

data class RpcBody(
        @Json(name = "method") val method: String,
        @Json(name = "arguments") val args: Map<String, Any>?)