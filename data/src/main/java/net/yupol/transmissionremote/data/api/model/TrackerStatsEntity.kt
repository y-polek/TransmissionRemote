package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class TrackerStatsEntity(
        @JvmField @Json(name = "id") val id: Int,
        @JvmField @Json(name = "announce") val announce: String,
        @JvmField @Json(name = "announceState") val announceState: Int,
        @JvmField @Json(name = "downloadCount") val downloadCount: Int,
        @JvmField @Json(name = "hasAnnounced") val hasAnnounced: Boolean,
        @JvmField @Json(name = "hasScraped") val hasScraped: Boolean,
        @JvmField @Json(name = "host") val host: String,
        @JvmField @Json(name = "isBackup") val isBackup: Boolean,
        @JvmField @Json(name = "lastAnnouncePeerCount") val lastAnnouncePeerCount: Int,
        @JvmField @Json(name = "lastAnnounceResult") val lastAnnounceResult: String,
        @JvmField @Json(name = "lastAnnounceStartTime") val lastAnnounceStartTime: Long,
        @JvmField @Json(name = "lastAnnounceSucceeded") val lastAnnounceSucceeded: Boolean,
        @JvmField @Json(name = "lastAnnounceTime") val lastAnnounceTime: Long,
        @JvmField @Json(name = "lastAnnounceTimedOut") val lastAnnounceTimedOut: Boolean,
        @JvmField @Json(name = "lastScrapeResult") val lastScrapeResult: String,
        @JvmField @Json(name = "lastScrapeStartTime") val lastScrapeStartTime: Long,
        @JvmField @Json(name = "lastScrapeSucceeded") val lastScrapeSucceeded: Boolean,
        @JvmField @Json(name = "lastScrapeTime") val lastScrapeTime: Long,
        @JvmField @Json(name = "lastScrapeTimedOut") val lastScrapeTimedOut: Byte,
        @JvmField @Json(name = "leecherCount") val leecherCount: Int,
        @JvmField @Json(name = "nextAnnounceTime") val nextAnnounceTime: Long,
        @JvmField @Json(name = "nextScrapeTime") val nextScrapeTime: Long,
        @JvmField @Json(name = "scrape") val scrape: String,
        @JvmField @Json(name = "scrapeState") val scrapeState: Int,
        @JvmField @Json(name = "seederCount") val seederCount: Int,
        @JvmField @Json(name = "tier") val tier: Int)
