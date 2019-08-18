package net.yupol.transmissionremote.data.repository

import net.yupol.transmissionremote.domain.repository.DownloadLocationRepository
import javax.inject.Inject

class DownloadLocationRepositoryImpl @Inject constructor() : DownloadLocationRepository {

    private val previousLocations = mutableListOf<String>()

    override fun addPreviousLocation(location: String) {
        if (previousLocations.containsIgnoreCase(location)) return

        previousLocations.add(location)
    }

    override fun removePreviousLocation(location: String) {
        previousLocations.removeAll { it.equals(location, ignoreCase = true) }
    }

    override fun getPreviousLocations() = previousLocations

    override fun pinLocation(location: String) {
        TODO("not implemented")
    }

    override fun unpinLocation(location: String) {
        TODO("not implemented")
    }

    override fun getPinnedLocations(): List<String> {
        TODO("not implemented")
    }

    override fun defaultDownloadLocation(): String {
        TODO("not implemented")
    }

    private fun List<String>.containsIgnoreCase(value: String): Boolean {
        return firstOrNull { it.equals(value, ignoreCase = true) } != null
    }
}
