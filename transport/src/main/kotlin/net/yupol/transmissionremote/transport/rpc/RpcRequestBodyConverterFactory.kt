package net.yupol.transmissionremote.transport.rpc

import com.squareup.moshi.Moshi
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class RpcRequestBodyConverterFactory(private val moshi: Moshi) : Converter.Factory() {

    companion object {
        fun create(moshi: Moshi) = RpcRequestBodyConverterFactory(moshi)
    }

    override fun requestBodyConverter(
            type: Type?, parameterAnnotations: Array<out Annotation>?,
            methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {

        val rpcMethod = methodAnnotations.find<RpcMethod>()?.name ?: return null
        val argName = parameterAnnotations.find<RpcArg>()?.name

        val adapter = moshi.adapter<RpcBody>(RpcBody::class.java)

        return when {
            argName != null -> RpcArgRequestBodyConverter(rpcMethod, argName, adapter)
            else -> RpcRequestBodyConverter(rpcMethod, adapter)
        }
    }
}

private inline fun <reified T> Array<out Annotation>?.find() = this?.find { it.annotationClass == T::class } as? T
