package net.yupol.transmissionremote.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import net.yupol.transmissionremote.data.api.rpc.RpcFailureException
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset
import javax.inject.Inject

class RpcFailureInterceptor @Inject constructor(moshi: Moshi) : Interceptor {

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }

    private val adapter = moshi.adapter(RpcResponse::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (response.code() != 200) return response

        val responseBody = response.body()!!
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer()

        val body = buffer.clone().readString(UTF8)

        val rpcResponse = adapter.fromJson(body)
        val result = rpcResponse?.result
        if (result != "success") throw RpcFailureException(result ?: "Request failed")

        return response
    }
}

data class RpcResponse(@Json(name = "result") val result: String)
