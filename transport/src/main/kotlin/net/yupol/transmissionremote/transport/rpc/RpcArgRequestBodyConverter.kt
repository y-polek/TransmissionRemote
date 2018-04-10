package net.yupol.transmissionremote.transport.rpc

import com.squareup.moshi.JsonAdapter
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

class RpcArgRequestBodyConverter(private val method: String, private val argName: String, private val adapter: JsonAdapter<RpcBody>) : Converter<Any, RequestBody> {

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    }

    override fun convert(argValue: Any): RequestBody {
        return RequestBody.create(MEDIA_TYPE, adapter.toJson(RpcBody(method, mapOf(argName to argValue))))
    }
}

