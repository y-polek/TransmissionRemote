package net.yupol.transmissionremote.domain.model

data class Server(
        @JvmField val name: String,
        @JvmField val host: ProtectedProperty<String>,
        @JvmField val port: Int?,
        @JvmField val https: Boolean,
        @JvmField val login: ProtectedProperty<String?>,
        @JvmField val password: ProtectedProperty<String?>,
        @JvmField val rpcPath: String,
        @JvmField val trustSelfSignedSslCert: Boolean)
{
    companion object {
        val EMPTY = Server(
                name = "",
                host = ProtectedProperty(""),
                port = null,
                https = false,
                login = ProtectedProperty(null),
                password = ProtectedProperty(null),
                rpcPath =  "",
                trustSelfSignedSslCert = false)
    }
}
