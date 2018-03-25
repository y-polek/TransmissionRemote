package net.yupol.transmissionremote.transport

import okhttp3.Interceptor
import okhttp3.Response

class SessionIdInterceptor: Interceptor {

    companion object {
        const val SESSION_ID_HEADER = "X-Transmission-Session-Id"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)
        if (response.code() != 409) return response

        val sessionId = response.header(SESSION_ID_HEADER) ?: return response

        return chain.proceed(request.newBuilder()
                .header(SESSION_ID_HEADER, sessionId)
                .build())
    }
}