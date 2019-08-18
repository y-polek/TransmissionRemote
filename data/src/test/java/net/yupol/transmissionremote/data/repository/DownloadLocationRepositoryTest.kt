package net.yupol.transmissionremote.data.repository

import org.assertj.core.api.Java6Assertions.*
import org.junit.Ignore
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
    @Ignore
    fun `List of Previous Locations is sorted alphabetically`() {
        repo.addPreviousLocation("~/Downloads")
        repo.addPreviousLocation("~/")
        repo.addPreviousLocation("~/Documents/Video")
        repo.addPreviousLocation("~/Documents/Photo")
        repo.addPreviousLocation("~/Documents")
        repo.addPreviousLocation("~/Documents/Video/Movies")

        assertThat(repo.getPreviousLocations()).containsExactly(
                "~/",
                "~/Documents",
                "~/Documents/Photo",
                "~/Documents/Video",
                "~/Documents/Video/Movies",
                "~/Downloads")
    }


    @Test
    @Ignore
    fun `List of Pinned Locations is empty at the beginning`() {
        assertThat(repo.getPinnedLocations()).isEmpty()
    }
}
