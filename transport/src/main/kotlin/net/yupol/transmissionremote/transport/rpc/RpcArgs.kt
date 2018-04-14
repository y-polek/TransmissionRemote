package net.yupol.transmissionremote.transport.rpc

import net.yupol.transmissionremote.model.Parameter

data class RpcArgs(val method: String, val arguments: Map<String, Any>? = null) {

    companion object {

        @JvmStatic
        fun sessionGet() = mapOf<String, Any>()

        @JvmStatic
        fun sessionSet(vararg params: Parameter<String, Any>) =
                params.map { it.key to it.value }.toMap()

        @JvmStatic
        fun sessionSet(params: List<Parameter<String, Any>>) = sessionSet(*params.toTypedArray())

        @JvmStatic
        fun renameTorrent(id: Int, path: String, name: String) =
                mapOf<String, Any>(
                        "ids" to intArrayOf(id),
                        "path" to path,
                        "name" to name)

        @JvmStatic
        fun setLocation(location: String, move: Boolean, vararg ids: Int) =
                mapOf<String, Any>(
                        "ids" to ids,
                        "location" to location,
                        "move" to move)

        @JvmStatic
        fun removeTorrents(deleteData: Boolean, vararg ids: Int) =
                mapOf<String, Any>(
                        "ids" to ids,
                        "delete-local-data" to deleteData)
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
