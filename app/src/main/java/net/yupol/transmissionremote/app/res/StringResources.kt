package net.yupol.transmissionremote.app.res

import android.content.res.Resources
import net.yupol.transmissionremote.app.R

interface StringResources {

    val networkErrorNoNetwork: String
    val networkErrorNoNetworkInAirplaneMode: String
    val networkErrorNoConnection: String
    val networkErrorUnauthorized: String
}

class StringResourcesImpl(res: Resources): StringResources {

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
}
