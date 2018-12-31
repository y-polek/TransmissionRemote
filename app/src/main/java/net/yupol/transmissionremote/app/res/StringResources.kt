package net.yupol.transmissionremote.app.res

import android.content.res.Resources
import net.yupol.transmissionremote.app.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResources @Inject constructor(private val res: Resources) {

    val networkErrorNoNetwork: String by lazy {
        res.getString(R.string.network_error_message_no_network)
    }

    val networkErrorNoNetworkInAirplaneMode: String by lazy {
        res.getString(R.string.network_error_message_no_network_in_airplane_mode)
    }

    val networkErrorNoConnection: String by lazy {
        res.getString(R.string.network_error_message_connection_error)
    }

    val networkErrorUnauthorized: String by lazy {
        res.getString(R.string.network_error_message_unauthorized)
    }

    fun torrentsCount(count: Int): String {
        return res.getQuantityString(R.plurals.torrents, count, count)
    }
}
