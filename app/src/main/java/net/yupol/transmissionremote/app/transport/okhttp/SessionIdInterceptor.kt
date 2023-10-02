package net.yupol.transmissionremote.app.transport.okhttp

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly
import javax.inject.Inject

class SessionIdInterceptor @Inject constructor(): Interceptor {

    private var sessionId: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().withSessionId()

        val response = chain.proceed(request)

        if (response.code == 409) {
            sessionId = response.header(SESSION_ID_HEADER)
            if (sessionId != null) {
                response.closeQuietly()
                return chain.proceed(request.withSessionId())
            }
        }

        return response
    }

    private fun Request.withSessionId(): Request {
        sessionId ?: return this
        return newBuilder().header(SESSION_ID_HEADER, sessionId.orEmpty()).build()
    }

    companion object {
        const val SESSION_ID_HEADER = "X-Transmission-Session-Id"
    }
}
