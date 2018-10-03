package net.yupol.transmissionremote.domain.model

data class Server(
        val name: String,
        val host: String,
        val port: Int?,
        val https: Boolean,
        val login: String?,
        val password: String?,
        val rpcPath: String,
        val trustSelfSignedSslCert: Boolean)
