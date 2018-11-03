package net.yupol.transmissionremote.domain.model

data class Server(
        val name: String,
        val host: ProtectedProperty<String>,
        val port: Int?,
        val https: Boolean,
        val login: ProtectedProperty<String?>,
        val password: ProtectedProperty<String?>,
        val rpcPath: String,
        val trustSelfSignedSslCert: Boolean)
