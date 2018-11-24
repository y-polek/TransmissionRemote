package net.yupol.transmissionremote.domain.model

data class Server(
        @JvmField val name: String, // TODO: server name mast be unique
        @JvmField val host: String,
        @JvmField val port: Int?,
        @JvmField val https: Boolean = false,
        @JvmField val login: String? = null,
        @JvmField val password: String? = null,
        @JvmField val rpcPath: String,
        @JvmField val trustSelfSignedSslCert: Boolean = false)
{
    companion object {
        val EMPTY = Server(
                name = "",
                host = "",
                port = null,
                rpcPath =  "")
    }

    fun authEnabled() = login != null

    override fun toString(): String {
        return "Server(" +
                "name='$name', " +
                "host='████████', " +
                "port=$port, " +
                "https=$https, " +
                "login=████████, " +
                "password=████████, " +
                "rpcPath='$rpcPath', " +
                "trustSelfSignedSslCert=$trustSelfSignedSslCert)"
    }
}
