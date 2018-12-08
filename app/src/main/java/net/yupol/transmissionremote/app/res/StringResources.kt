package net.yupol.transmissionremote.app.res

import android.content.res.Resources
import net.yupol.transmissionremote.app.R
import javax.inject.Inject
import javax.inject.Singleton

interface StringResources {

    val networkErrorNoNetwork: String
    val networkErrorNoNetworkInAirplaneMode: String
    val networkErrorNoConnection: String
    val networkErrorUnauthorized: String

    fun torrentsCount(count: Int): String
}

@Singleton
class StringResourcesImpl @Inject constructor(private val res: Resources): StringResources {

    override val networkErrorNoNetwork: String by lazy {
        res.getString(R.string.network_error_message_no_network)
    }

    override val networkErrorNoNetworkInAirplaneMode: String by lazy {
        res.getString(R.string.network_error_message_no_network_in_airplane_mode)
    }

    override val networkErrorNoConnection: String by lazy {
        res.getString(R.string.network_error_message_connection_error)
    }

    override val networkErrorUnauthorized: String by lazy {
        res.getString(R.string.network_error_message_unauthorized)
    }

    override fun torrentsCount(count: Int): String {
        return res.getQuantityString(R.plurals.torrents, count, count)
    }
}
