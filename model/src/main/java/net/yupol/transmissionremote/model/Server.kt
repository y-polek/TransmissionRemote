package net.yupol.transmissionremote.model

data class Server(
        val name: String,
        val host: String,
        val port: Int = 9091,
        val useHttps: Boolean = false,
        val trustSelfSignedSslCert: Boolean = false,
        val login: String = "",
        val password: String = "",
        val rpcUrl: String = "transmission/rpc"
)

fun Server.baseUrl(): String {
    return with(StringBuilder()) {
        append(if (useHttps) "https" else "http")
        append("://")
        append(host)
        append(':')
        append(port)
        append('/')
        append(rpcUrl)
        append('/')
        toString()
    }
}