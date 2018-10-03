package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class TrackerStatsEntity(
        @Json(name = "id") val id: Int,
        @Json(name = "announce") val announce: String,
        @Json(name = "announceState") val announceState: Int,
        @Json(name = "downloadCount") val downloadCount: Int,
        @Json(name = "hasAnnounced") val hasAnnounced: Boolean,
        @Json(name = "hasScraped") val hasScraped: Boolean,
        @Json(name = "host") val host: String,
        @Json(name = "isBackup") val isBackup: Boolean,
        @Json(name = "lastAnnouncePeerCount") val lastAnnouncePeerCount: Int,
        @Json(name = "lastAnnounceResult") val lastAnnounceResult: String,
        @Json(name = "lastAnnounceStartTime") val lastAnnounceStartTime: Long,
        @Json(name = "lastAnnounceSucceeded") val lastAnnounceSucceeded: Boolean,
        @Json(name = "lastAnnounceTime") val lastAnnounceTime: Long,
        @Json(name = "lastAnnounceTimedOut") val lastAnnounceTimedOut: Boolean,
        @Json(name = "lastScrapeResult") val lastScrapeResult: String,
        @Json(name = "lastScrapeStartTime") val lastScrapeStartTime: Long,
        @Json(name = "lastScrapeSucceeded") val lastScrapeSucceeded: Boolean,
        @Json(name = "lastScrapeTime") val lastScrapeTime: Long,
        @Json(name = "lastScrapeTimedOut") val lastScrapeTimedOut: Byte,
        @Json(name = "leecherCount") val leecherCount: Int,
        @Json(name = "nextAnnounceTime") val nextAnnounceTime: Long,
        @Json(name = "nextScrapeTime") val nextScrapeTime: Long,
        @Json(name = "scrape") val scrape: String,
        @Json(name = "scrapeState") val scrapeState: Int,
        @Json(name = "seederCount") val seederCount: Int,
        @Json(name = "tier") val tier: Int
)