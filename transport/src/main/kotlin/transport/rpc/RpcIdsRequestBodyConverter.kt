package transport.rpc

import com.squareup.moshi.JsonAdapter
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter

class RpcIdsRequestBodyConverter(private val method: String, private val adapter: JsonAdapter<RpcBody>) : Converter<IntArray, RequestBody> {

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
    }

    override fun convert(ids: IntArray): RequestBody {
        return RequestBody.create(MEDIA_TYPE, adapter.toJson(RpcBody(method, mapOf("ids" to ids))))
    }
}

