package net.yupol.transmissionremote.domain.model

data class Server(
        @JvmField val name: String, // TODO: server name mast be unique
        @JvmField val host: ProtectedProperty<String>,
        @JvmField val port: Int?,
        @JvmField val https: Boolean = false,
        @JvmField val login: ProtectedProperty<String?> = ProtectedProperty(null),
        @JvmField val password: ProtectedProperty<String?> = ProtectedProperty(null),
        @JvmField val rpcPath: String,
        @JvmField val trustSelfSignedSslCert: Boolean = false)
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

    fun authEnabled() = login.value != null
}
