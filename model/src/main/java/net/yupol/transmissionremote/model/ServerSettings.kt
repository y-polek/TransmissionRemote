package net.yupol.transmissionremote.model

import com.squareup.moshi.Json

data class ServerSettings(
        @Json(name = "speed-limit-down") val speedLimitDown: Int,
        @Json(name = "speed-limit-down-enabled") val speedLimitDownEnabled: Boolean,
        @Json(name = "speed-limit-up") val speedLimitUp: Int,
        @Json(name = "speed-limit-up-enabled") val speedLimitUpEnabled: Boolean,
        @Json(name = "alt-speed-down") val altSpeedLimitDown: Int,
        @Json(name = "alt-speed-up") val altSpeedLimitUp: Int,
        @Json(name = "alt-speed-enabled") val altSpeedLimitEnabled: Boolean,
        @Json(name = "download-dir") val downloadDir: String,
        @Json(name = "seedRatioLimited") val seedRatioLimited: Boolean,
        @Json(name = "seedRatioLimit") val seedRatioLimit: Double,
        @Json(name = "idle-seeding-limit-enabled") val seedIdleLimited: Boolean,
        @Json(name = "idle-seeding-limit") val seedIdleLimit: Int,
        @Json(name = "download-dir-free-space") val downloadDirFreeSpace: Long,
        @Json(name = "version") val version: String
)