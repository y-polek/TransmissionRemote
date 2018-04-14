package net.yupol.transmissionremote.transport.rpc

import com.squareup.moshi.JsonAdapter
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

class RpcArgRequestBodyConverter(
        private val method: String,
        private val fields: Array<String>,
        private val argName: String,
        private val adapter: JsonAdapter<RpcBody>
) : Converter<Any, RequestBody> {

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    }

    override fun convert(argValue: Any): RequestBody {
        val args = mutableMapOf<String, Any>().apply {
            if (argValue.isNonEmptyArray()) {
                put(argName, argValue)
            }
            if (fields.isNotEmpty()) {
                put("fields", fields)
            }
        }
        return RequestBody.create(MEDIA_TYPE, adapter.toJson(RpcBody(method, args)))
    }
}

private fun Any.isNonEmptyArray(): Boolean {
    return when (this) {
        is Array<*> -> this.isNotEmpty()
        is ByteArray -> this.isNotEmpty()
        is ShortArray -> this.isNotEmpty()
        is IntArray -> this.isNotEmpty()
        is LongArray -> this.isNotEmpty()
        is FloatArray -> this.isNotEmpty()
        is DoubleArray -> this.isNotEmpty()
        is BooleanArray -> this.isNotEmpty()
        is CharArray -> this.isNotEmpty()
        else -> false
    }
}

