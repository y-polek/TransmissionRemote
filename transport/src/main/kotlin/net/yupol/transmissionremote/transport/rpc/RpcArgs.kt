package net.yupol.transmissionremote.transport.rpc

import net.yupol.transmissionremote.model.Parameter
import net.yupol.transmissionremote.model.TorrentMetadata.*

data class RpcArgs(val method: String, val arguments: Map<String, Any>? = null) {

    companion object {

        @JvmStatic
        fun torrentGet(vararg ids: Int): Map<String, Any> {

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

            return args
        }

        @JvmStatic
        fun sessionGet() = mapOf<String, Any>()

        @JvmStatic
        fun sessionSet(vararg params: Parameter<String, Any>) = params.map { it.key to it.value }.toMap()

        @JvmStatic
        fun sessionSet(params: List<Parameter<String, Any>>) = sessionSet(*params.toTypedArray())

        @JvmStatic
        fun renameTorrent(id: Int, path: String, name: String) = mapOf<String, Any>("ids" to intArrayOf(id), "path" to path, "name" to name)

        @JvmStatic
        fun setLocation(location: String, move: Boolean, vararg ids: Int) = mapOf<String, Any>("ids" to ids, "location" to location, "move" to move)
    }
}

object SessionParameters {

    @JvmStatic
    fun altSpeedLimitEnabled(enabled: Boolean) = Parameter("alt-speed-enabled", enabled)

    @JvmStatic
    fun altSpeedLimitDown(limit: Long) = Parameter("alt-speed-down", limit)

    @JvmStatic
    fun altSpeedLimitUp(limit: Long) = Parameter("alt-speed-up", limit)

    @JvmStatic
    fun speedLimitDownEnabled(enabled: Boolean) = Parameter("speed-limit-down-enabled", enabled)

    @JvmStatic
    fun speedLimitDown(limit: Long) = Parameter("speed-limit-down", limit)

    @JvmStatic
    fun speedLimitUpEnabled(enabled: Boolean) = Parameter("speed-limit-up-enabled", enabled)

    @JvmStatic
    fun speedLimitUp(limit: Long) = Parameter("speed-limit-up", limit)
}
