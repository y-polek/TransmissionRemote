package net.yupol.transmissionremote.transport


data class RpcRequest(val method: String, val arguments: Map<String, Any>? = null) {

    companion object {

        fun torrentGet() = RpcRequest("torrent-get", mapOf("fields" to listOf("id", "name")))

        fun sessionGet() = RpcRequest("session-get")
    }
}