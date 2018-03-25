package net.yupol.transmissionremote.transport


data class RpcRequest(val method: String, val arguments: Map<String, Any>? = null) {

    companion object {

        fun torrentGet(): RpcRequest = RpcRequest("torrent-get", mapOf("fields" to listOf("id", "name")))
    }
}