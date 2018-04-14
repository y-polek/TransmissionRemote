package net.yupol.transmissionremote.transport.rpc

import com.squareup.moshi.JsonAdapter
import net.yupol.transmissionremote.utils.emptyToNull
import net.yupol.transmissionremote.utils.isEmptyArray
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

class RpcArgRequestBodyConverter(
        private val method: String,
        private val staticArgs: Map<String, Any>,
        private val argName: String,
        private val adapter: JsonAdapter<RpcBody>
) : Converter<Any, RequestBody> {

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    }

    override fun convert(argValue: Any): RequestBody {
        val args = mutableMapOf<String, Any>().apply {
            putAll(staticArgs)
            if (!argValue.isEmptyArray()) {
                put(argName, argValue)
            }
        }
        return RequestBody.create(MEDIA_TYPE, adapter.toJson(RpcBody(method, args.emptyToNull())))
    }
}

