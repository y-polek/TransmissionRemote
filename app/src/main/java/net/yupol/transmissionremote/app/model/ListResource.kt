package net.yupol.transmissionremote.app.model

data class ListResource<out T>(
        @JvmField val status: Status,
        @JvmField val data: List<T>? = null,
        @JvmField val error: Throwable? = null,
        @JvmField val inAirplaneMode: Boolean = false)
{
    companion object {
        @JvmStatic fun <T> success(data: List<T>) = ListResource(Status.SUCCESS, data)
        @JvmStatic fun <T> error(error: Throwable) = ListResource<T>(Status.ERROR, error = error)
        @JvmStatic fun <T> noNetwork(inAirplaneMode: Boolean) = ListResource<T>(Status.NO_NETWORK, inAirplaneMode = inAirplaneMode)
    }
}
