package net.yupol.transmissionremote.transport

import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

class AuthenticationInterceptor(private val login: String, private val password: String): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
                .header("Authorization", authHeader(login, password))
                .build()
        return chain.proceed(request)
    }

    private fun authHeader(login: String, password: String): String {
        val credentials = Base64.getEncoder().encodeToString("$login:$password".toByteArray())
        return "Basic $credentials"
    }
}