package net.yupol.transmissionremote.data.repository

import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class DownloadLocationRepositoryTest {

    private val repo = DownloadLocationRepositoryImpl()

    @Test
    fun `List of Previous Locations is empty at the beginning`() {
        assertThat(repo.getPreviousLocations()).isEmpty()
    }

    @Test
    fun `List of Previous Locations contain added locations`() {
        repo.addPreviousLocation("~/Downloads")

        assertThat(repo.getPreviousLocations()).containsOnly("~/Downloads")
    }

    @Test
    fun `List of Previous Locations does not contain duplicates`() {
        repo.addPreviousLocation("~/Downloads")
        repo.addPreviousLocation("~/Desktop")
        repo.addPreviousLocation("~/Downloads")

        assertThat(repo.getPreviousLocations()).hasSize(2)
        assertThat(repo.getPreviousLocations()).containsOnly("~/Downloads", "~/Desktop")
    }

    @Test
    fun `List of Previous Location is case insensitive`() {
        repo.addPreviousLocation("~/Downloads/iso")
        repo.addPreviousLocation("~/Downloads")
        repo.addPreviousLocation("~/downloads")

        assertThat(repo.getPreviousLocations()).hasSize(2)
        assertThat(repo.getPreviousLocations()).containsExactlyInAnyOrder("~/Downloads", "~/Downloads/iso")
    }

    @Test
    fun `List of Previous Location does not contain removed location`() {
        repo.addPreviousLocation("~/")
        repo.addPreviousLocation("~/Video")
        repo.addPreviousLocation("~/Downloads")

        repo.removePreviousLocation("~/Video")

        assertThat(repo.getPreviousLocations()).containsExactlyInAnyOrder("~/", "~/Downloads")
    }

    @Test
    fun `removePreviousLocation() is case insensitive`() {
        repo.addPreviousLocation("~/Downloads")
        repo.addPreviousLocation("~/")

        repo.removePreviousLocation("~/downloads")

        assertThat(repo.getPreviousLocations()).containsOnly("~/")
    }

    @Test
    fun `getDefaultDownloadLocation() returns empty string if default location not set`() {
        assertThat(repo.getDefaultDownloadLocation()).isEmpty()
    }

    @Test
    fun `getDefaultDownloadLocation() returns location passed via setDefaultDownloadLocation()`() {
        repo.setDefaultDownloadLocation("~/Downloads")

        assertThat(repo.getDefaultDownloadLocation()).isEqualTo("~/Downloads")
    }

    @Test
    fun `List of Pinned Locations is empty at the beginning`() {
        assertThat(repo.getPinnedLocations()).isEmpty()
    }

    @Test
    fun `pinLocation() adds location to Pinned list`() {
        repo.pinLocation("~/Downloads")
        repo.pinLocation("~/")

        assertThat(repo.getPinnedLocations()).hasSize(2)
        assertThat(repo.getPinnedLocations()).containsExactlyInAnyOrder("~/Downloads", "~/")
    }

    @Test
    fun `unpinLocation() removes location from Pinned list`() {
        repo.pinLocation("~/Downloads")
        repo.pinLocation("~/")

        repo.unpinLocation("~/Downloads")

        assertThat(repo.getPinnedLocations()).hasSize(1)
        assertThat(repo.getPinnedLocations()).containsOnly("~/")
    }

    @Test
    fun `pinLocation() is idempotent`() {
        repo.pinLocation("~/Downloads")

        repo.pinLocation("~/Downloads")

        assertThat(repo.getPinnedLocations()).hasSize(1)
        assertThat(repo.getPinnedLocations()).containsOnly("~/Downloads")
    }

    @Test
    fun `Calling unpinLocation() for missing location takes no effect`() {
        repo.unpinLocation("~/Downloads")

        assertThat(repo.getPinnedLocations()).isEmpty()
        assertThat(repo.getPreviousLocations()).isEmpty()
    }

    @Test
    fun `pinLocation() is case insensitive`() {
        repo.pinLocation("~/DOWNLOADS")

        repo.pinLocation("~/downloads")

        assertThat(repo.getPinnedLocations()).hasSize(1)
    }

    @Test
    fun `unpinLocation() is case insensitive`() {
        repo.pinLocation("~/DOWNLOADS")

        repo.unpinLocation("~/downloads")

        assertThat(repo.getPinnedLocations()).isEmpty()
    }
}
