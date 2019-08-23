package net.yupol.transmissionremote.data.repository

import net.yupol.transmissionremote.domain.repository.DownloadLocationRepository
import javax.inject.Inject

class DownloadLocationRepositoryImpl @Inject constructor() : DownloadLocationRepository {

    private var defaultLocation = ""
    private val previousLocations = mutableListOf<String>()
    private val pinnedLocations = mutableListOf<String>()

    override fun addPreviousLocation(location: String) {
        if (previousLocations.containsIgnoreCase(location) || pinnedLocations.containsIgnoreCase(location)) return

        previousLocations.add(location)
    }

    override fun removePreviousLocation(location: String) {
        previousLocations.removeAll { it.equals(location, ignoreCase = true) }
    }

    override fun getPreviousLocations() = previousLocations

    override fun pinLocation(location: String) {
        if (pinnedLocations.containsIgnoreCase(location)) return

        pinnedLocations.add(location)
        previousLocations.removeAll { it.equals(location, ignoreCase = true) }
    }

    override fun unpinLocation(location: String) {
        val removed = pinnedLocations.removeAll { it.equals(location, ignoreCase = true) }
        if (removed) {
            addPreviousLocation(location)
        }
    }

    override fun getPinnedLocations(): List<String> {
        return pinnedLocations
    }

    override fun setDefaultDownloadLocation(location: String) {
        defaultLocation = location
    }

    override fun getDefaultDownloadLocation(): String {
        return defaultLocation
    }

    private fun List<String>.containsIgnoreCase(value: String): Boolean {
        return firstOrNull { it.equals(value, ignoreCase = true) } != null
    }
}
