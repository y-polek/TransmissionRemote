package net.yupol.transmissionremote.app.analytics

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TorrentCountTest {

    @Test
    fun `0 torrents`() {
        assertThat(TorrentCount.fromCount(0).value).isEqualTo("0")
    }

    @Test
    fun `1 torrent`() {
        assertThat(TorrentCount.fromCount(1).value).isEqualTo("1")
    }

    @Test
    fun `2-5 torrents`() {
        assertThat(TorrentCount.fromCount(2).value).isEqualTo("2-5")
        assertThat(TorrentCount.fromCount(4).value).isEqualTo("2-5")
        assertThat(TorrentCount.fromCount(5).value).isEqualTo("2-5")
    }

    @Test
    fun `6-10 torrents`() {
        assertThat(TorrentCount.fromCount(6).value).isEqualTo("6-10")
        assertThat(TorrentCount.fromCount(8).value).isEqualTo("6-10")
        assertThat(TorrentCount.fromCount(10).value).isEqualTo("6-10")
    }

    @Test
    fun `11-20 torrents`() {
        assertThat(TorrentCount.fromCount(11).value).isEqualTo("11-20")
        assertThat(TorrentCount.fromCount(15).value).isEqualTo("11-20")
        assertThat(TorrentCount.fromCount(20).value).isEqualTo("11-20")
    }

    @Test
    fun `21-30 torrents`() {
        assertThat(TorrentCount.fromCount(21).value).isEqualTo("21-30")
        assertThat(TorrentCount.fromCount(25).value).isEqualTo("21-30")
        assertThat(TorrentCount.fromCount(30).value).isEqualTo("21-30")
    }

    @Test
    fun `31-40 torrents`() {
        assertThat(TorrentCount.fromCount(31).value).isEqualTo("31-40")
        assertThat(TorrentCount.fromCount(35).value).isEqualTo("31-40")
        assertThat(TorrentCount.fromCount(40).value).isEqualTo("31-40")
    }

    @Test
    fun `41-50 torrents`() {
        assertThat(TorrentCount.fromCount(41).value).isEqualTo("41-50")
        assertThat(TorrentCount.fromCount(45).value).isEqualTo("41-50")
        assertThat(TorrentCount.fromCount(50).value).isEqualTo("41-50")
    }

    @Test
    fun `51-100 torrents`() {
        assertThat(TorrentCount.fromCount(51).value).isEqualTo("51-100")
        assertThat(TorrentCount.fromCount(75).value).isEqualTo("51-100")
        assertThat(TorrentCount.fromCount(100).value).isEqualTo("51-100")
    }

    @Test
    fun `101-200 torrents`() {
        assertThat(TorrentCount.fromCount(101).value).isEqualTo("101-200")
        assertThat(TorrentCount.fromCount(150).value).isEqualTo("101-200")
        assertThat(TorrentCount.fromCount(200).value).isEqualTo("101-200")
    }

    @Test
    fun `201-500 torrents`() {
        assertThat(TorrentCount.fromCount(201).value).isEqualTo("201-500")
        assertThat(TorrentCount.fromCount(300).value).isEqualTo("201-500")
        assertThat(TorrentCount.fromCount(500).value).isEqualTo("201-500")
    }

    @Test
    fun `501-1000 torrents`() {
        assertThat(TorrentCount.fromCount(501).value).isEqualTo("501-1000")
        assertThat(TorrentCount.fromCount(750).value).isEqualTo("501-1000")
        assertThat(TorrentCount.fromCount(1000).value).isEqualTo("501-1000")
    }

    @Test
    fun `1001-2000 torrents`() {
        assertThat(TorrentCount.fromCount(1001).value).isEqualTo("1001-2000")
        assertThat(TorrentCount.fromCount(1500).value).isEqualTo("1001-2000")
        assertThat(TorrentCount.fromCount(2000).value).isEqualTo("1001-2000")
    }

    @Test
    fun `2001+ torrents`() {
        assertThat(TorrentCount.fromCount(2001).value).isEqualTo("2001+")
        assertThat(TorrentCount.fromCount(3000).value).isEqualTo("2001+")
        assertThat(TorrentCount.fromCount(Int.MAX_VALUE).value).isEqualTo("2001+")
    }

    @Test
    fun `negative torrents count`() {
        assertThat(TorrentCount.fromCount(-1).value).isEqualTo("Unknown")
        assertThat(TorrentCount.fromCount(Int.MIN_VALUE).value).isEqualTo("Unknown")
    }
}
