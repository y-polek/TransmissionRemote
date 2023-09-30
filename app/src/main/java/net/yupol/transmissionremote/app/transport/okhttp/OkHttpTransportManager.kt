package net.yupol.transmissionremote.app.transport.okhttp

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.google.api.client.json.JsonObjectParser
import com.google.api.client.json.jackson2.JacksonFactory
import com.octo.android.robospice.persistence.exception.SpiceException
import com.octo.android.robospice.request.listener.RequestListener
import net.yupol.transmissionremote.app.BuildConfig
import net.yupol.transmissionremote.app.analytics.Analytics
import net.yupol.transmissionremote.app.server.Server
import net.yupol.transmissionremote.app.transport.TransportManager
import net.yupol.transmissionremote.app.transport.request.Request
import net.yupol.transmissionremote.app.transport.request.ResponseFailureException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.io.StringReader
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class OkHttpTransportManager(
    private val server: Server,
    private val analytics: Analytics
) : TransportManager {

    private val okHttpClient = OkHttpClient.Builder().apply {
        dispatcher(Dispatcher())
        addInterceptor(SessionIdInterceptor())
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        if (server.isAuthenticationEnabled) {
            authenticator(BasicAuthenticator(server.userName.orEmpty(), server.password.orEmpty()))
        }
        if (server.trustSelfSignedSslCert) {
            // Create a trust manager that does not validate certificate chains
            @SuppressLint("CustomX509TrustManager")
            val trustAllManager = object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf(trustAllManager), SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory(sslContext.socketFactory, trustAllManager)
            hostnameVerifier { _, _ -> true }
        }
    }.build()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun <T : Any?> doRequest(request: Request<T>, listener: RequestListener<T>?) {
        analytics.logOkHttpRequestStart(request.javaClass)
        request.server = server
        val url = HttpUrl.Builder().apply {
            scheme(if (server.useHttps()) "https" else "http")
            port(server.port)
            host(server.host)
            if (server.rpcUrl.isNotBlank()) {
                val rpcPath = server.rpcUrl.trim('/', '\\')
                addPathSegments("$rpcPath/")
            }
        }.build()
        val okHttpRequest = okhttp3.Request.Builder()
            .post(request.createBody().toRequestBody(MEDIA_TYPE_JSON))
            .url(url)
            .build()

        okHttpClient.newCall(okHttpRequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    request.setResponse(response.code, responseBody)
                    if (response.code in 200..299) {
                        val responseJson = responseBody?.let(::JSONObject)
                        val resultStatus = responseJson?.getString("result")
                        if (resultStatus != "success") {
                            throw ResponseFailureException(resultStatus)
                        }
                        if (listener != null) {
                            val arguments = responseJson.getString("arguments")
                            val result = JSON_PARSER.parseAndClose(
                                StringReader(arguments),
                                request.resultType
                            )
                            analytics.logOkHttpRequestSuccess(request.javaClass)
                            notifyListenerSuccess(result, listener)
                        }
                    } else {
                        val error = SpiceException("Request failed with ${response.code} HTTP status code")
                        request.error = error
                        notifyListenerFailure(request, error, listener)
                    }
                } catch (e: Throwable) {
                    request.error = e
                    notifyListenerFailure(
                        request,
                        SpiceException("Failed to execute request", e),
                        listener
                    )
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                val error = SpiceException(e.message, e)
                request.error = error
                notifyListenerFailure(request, error, listener)
            }
        })
    }

    private fun <T : Any?> notifyListenerSuccess(result: T, listener: RequestListener<T>?) {
        listener ?: return
        mainThreadHandler.post {
            listener.onRequestSuccess(result)
        }
    }

    private fun <T : Any?> notifyListenerFailure(request: Request<T>, error: SpiceException, listener: RequestListener<T>?) {
        analytics.logOkHttpRequestFailure(request.javaClass)
        listener ?: return
        mainThreadHandler.post {
            listener.onRequestFailure(error)
        }
    }

    override fun <T : Any?> doRequest(
        request: Request<T>,
        listener: RequestListener<T>?,
        delay: Long
    ) {
        Handler(Looper.getMainLooper()).postDelayed(
            { doRequest(request, listener) },
            delay
        )
    }

    override fun isStarted(): Boolean {
        return true
    }

    companion object {
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
        private val JSON_PARSER = JsonObjectParser.Builder(JacksonFactory.getDefaultInstance()).build()
    }
}
