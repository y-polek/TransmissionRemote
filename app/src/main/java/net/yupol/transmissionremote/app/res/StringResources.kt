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

    val unknownError: String by lazy {
        res.getString(R.string.unknown_error_message)
    }

    fun torrentsCount(count: Int): String {
        return res.getQuantityString(R.plurals.torrents, count, count)
    }

    fun torrentFileSizeSummary(filesCount: Int, totalSize: String, selectedSize: String): String {
        return res.getString(R.string.torrent_file_size_summary, filesCount, totalSize, selectedSize)
    }

    fun freeSpace(size: String): String {
        return res.getString(R.string.free_space, size)
    }
}
