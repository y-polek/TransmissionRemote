package transport

import net.yupol.transmissionremote.model.TorrentMetadata.*

data class RpcRequest(val method: String, val arguments: Map<String, Any>? = null) {

    companion object {

        @JvmStatic
        fun torrentGet(vararg ids: Int): RpcRequest {

            val args: MutableMap<String, Any> = mutableMapOf()

            args["fields"] = listOf(
                    ID,
                    NAME,
                    PERCENT_DONE,
                    TOTAL_SIZE,
                    ADDED_DATE,
                    STATUS,
                    RATE_DOWNLOAD,
                    RATE_UPLOAD,
                    UPLOADED_EVER,
                    UPLOAD_RATIO,
                    ETA,
                    ERROR,
                    ERROR_STRING,
                    IS_FINISHED,
                    SIZE_WHEN_DONE,
                    LEFT_UNTIL_DONE,
                    PEERS_GETTING_FROM_US,
                    PEERS_SENDING_TO_US,
                    WEBSEEDS_SENDING_TO_US,
                    QUEUE_POSITION,
                    RECHECK_PROGRESS,
                    DONE_DATE
            )

            if (ids.isNotEmpty()) {
                args["ids"] = ids
            }

            return RpcRequest("torrent-get", args)
        }

        @JvmStatic
        fun sessionGet() = RpcRequest("session-get")
    }
}