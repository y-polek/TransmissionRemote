package net.yupol.transmissionremote.app.e2e.mockserver

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@DslMarker
annotation class NodeMarker

@NodeMarker
class RequestMap {

    val requests = mutableListOf<Request>()

    infix fun String.to(setup: Params.() -> Unit) {
        val params = Params()
        params.setup()
        requests.add(
            Request(
                method = this,
                ids = params.ids,
                response = Response().apply(params.response)
            )
        )
    }
}

@NodeMarker
class Params {
    var ids: List<Int> = emptyList()
    var response: Response.() -> Unit = {}
}

data class Request(
    val method: String,
    val ids: List<Int>,
    val response: Response
)

data class Response(
    var code: Int = 200,
    var filePath: String? = null,
    var delay: Duration? = null
)

private data class RequestBody(
    val method: String,
    val arguments: Arguments?
)

private data class Arguments(
    val ids: List<Int>?
)

fun mockWebServer(setup: RequestMap.() -> Unit): MockWebServer {
    val requestMap = RequestMap()
    requestMap.setup()
    val server = MockWebServer()
    server.dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            Log.d("MockWebServer", "-".repeat(120))
            val requestBodyStr = request.body.readUtf8()
            Log.d("MockWebServer", "Request: ${request.requestLine} $requestBodyStr")
            val requestBody = Gson().fromJson(requestBodyStr, RequestBody::class.java)
            val response = requestMap.requests
                .filter { (method, ids) ->
                    method == requestBody.method && ids == requestBody.arguments?.ids.orEmpty()
                }
                .map { it.response }
                .firstOrNull()
            Log.d("MockWebServer", "Response: $response")
            Log.d("MockWebServer", "-".repeat(120))
            return response?.toMockResponse()
                ?: throw IllegalStateException("Unknown request: ${request.requestLine} $requestBodyStr")
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
