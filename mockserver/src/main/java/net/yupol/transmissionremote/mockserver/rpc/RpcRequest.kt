package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class RpcRequest(
    @SerialName("arguments") val arguments: JsonObject?,
    @SerialName("method") val method: String
) {
    fun getStringArgument(key: String): String? {
        val value = arguments?.get(key) ?: return null
        if (value is JsonPrimitive && value.isString) {
            return value.jsonPrimitive.content
        } else {
            throw IllegalStateException("'$key' is not a String: $value")
        }
    }

    fun getBooleanArgument(key: String): Boolean? {
        val value = arguments?.get(key) ?: return null
        return value.jsonPrimitive.boolean
    }

    fun getIntArrayArgument(key: String): List<Int>? {
        val value = arguments?.get(key) ?: return null
        return value.jsonArray.map { it.jsonPrimitive.int }
    }
}
