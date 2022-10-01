package net.yupol.transmissionremote.app.e2e.mockserver

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@DslMarker
annotation class NodeMarker

@NodeMarker
class ResponseMap {

    val responses = mutableMapOf<String, Response>()

    infix fun String.to(setup: Response.() -> Unit) {
        val response = Response()
        response.setup()
        responses[this] = response
    }
}

@NodeMarker
class Response {
    var code: Int = 200
    var filePath: String? = null
    var delay: Duration? = null
}

private data class RequestBody(
    val method: String
)

fun mockWebServer(setup: ResponseMap.() -> Unit): MockWebServer {
    val responseMap = ResponseMap()
    responseMap.setup()
    val server = MockWebServer()
    server.dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val requestBody = request.body.readUtf8()
            val method = try {
                Gson().fromJson(requestBody, RequestBody::class.java).method
            } catch (e: JsonSyntaxException) {
                ""
            }

            return responseMap.responses[method]?.toMockResponse()
                ?: throw IllegalStateException("Unknown request: ${request.requestLine} $requestBody")
        }
    }
    return server
}

private fun readFile(filePath: String): String {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    return instrumentation.context.assets.open("mock-responses/$filePath")
        .bufferedReader()
        .use { it.readText() }
}

private fun Response.toMockResponse() = MockResponse().apply {
    setResponseCode(code)
    filePath?.let { setBody(readFile(it)) }
    delay?.let { setHeadersDelay(it.inWholeMilliseconds, TimeUnit.MILLISECONDS) }
}
