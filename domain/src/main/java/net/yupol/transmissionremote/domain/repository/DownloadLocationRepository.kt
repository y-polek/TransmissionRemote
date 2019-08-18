package net.yupol.transmissionremote.domain.repository

interface DownloadLocationRepository {

    fun addPreviousLocation(location: String)

    fun removePreviousLocation(location: String)

    fun getPreviousLocations(): List<String>

    fun pinLocation(location: String)

    fun unpinLocation(location: String)

    fun getPinnedLocations(): List<String>

    fun setDefaultDownloadLocation(location: String)

    fun getDefaultDownloadLocation(): String
}
