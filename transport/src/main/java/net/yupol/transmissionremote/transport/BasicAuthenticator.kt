package net.yupol.transmissionremote.transport

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.*

class BasicAuthenticator(private val login: String, private val password: String): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val authHeaderName = when (response.code()) {
            401 -> "Authorization"
            407 -> "Proxy-Authorization"
            else -> throw IllegalStateException("Unknown status code: ${response.code()}")
        }
        return requestWithAuthentication(response.request(), authHeaderName)
    }

    private fun requestWithAuthentication(request: Request, authHeaderName: String): Request? {
        if (request.header(authHeaderName) != null) return null // Give up, we've already failed to authenticate

        return request.newBuilder()
                .header(authHeaderName, authHeader(login, password))
                .build()
    }

    private fun authHeader(login: String, password: String): String {
        val credentials = Base64.getEncoder().encodeToString("$login:$password".toByteArray())
        return "Basic $credentials"
    }
}