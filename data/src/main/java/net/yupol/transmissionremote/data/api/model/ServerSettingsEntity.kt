package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class ServerSettingsEntity(
        @JvmField @Json(name = SPEED_LIMIT_DOWN) val speedLimitDown: Int,
        @JvmField @Json(name = SPEED_LIMIT_DOWN_ENABLED) val speedLimitDownEnabled: Boolean,
        @JvmField @Json(name = SPEED_LIMIT_UP)  val speedLimitUp: Int,
        @JvmField @Json(name = SPEED_LIMIT_UP_ENABLED)  val speedLimitUpEnabled: Boolean,
        @JvmField @Json(name = ALT_SPEED_LIMIT_DOWN)  val altSpeedLimitDown: Int,
        @JvmField @Json(name = ALT_SPEED_LIMIT_UP)  val altSpeedLimitUp: Int,
        @JvmField @Json(name = ALT_SPEED_LIMIT_ENABLED)  val altSpeedLimitEnabled: Boolean,
        @JvmField @Json(name = DOWNLOAD_DIR)  val downloadDir: String,
        @JvmField @Json(name = SEED_RATIO_LIMITED)  val seedRatioLimited: Boolean,
        @JvmField @Json(name = SEED_RATIO_LIMIT)  val seedRatioLimit: Double,
        @JvmField @Json(name = SEED_IDLE_LIMITED)  val seedIdleLimited: Boolean,
        @JvmField @Json(name = SEED_IDLE_LIMIT)  val seedIdleLimit: Int,
        @JvmField @Json(name = DOWNLOAD_DIR_FREE_SPACE)  val downloadDirFreeSpace: Long,
        @JvmField @Json(name = VERSION)  val version: String)
{
    companion object {
        const val SPEED_LIMIT_DOWN = "speed-limit-down"
        const val SPEED_LIMIT_DOWN_ENABLED = "speed-limit-down-enabled"
        const val SPEED_LIMIT_UP = "speed-limit-up"
        const val SPEED_LIMIT_UP_ENABLED = "speed-limit-up-enabled"
        const val ALT_SPEED_LIMIT_DOWN = "alt-speed-down"
        const val ALT_SPEED_LIMIT_UP = "alt-speed-up"
        const val ALT_SPEED_LIMIT_ENABLED = "alt-speed-enabled"
        const val DOWNLOAD_DIR = "download-dir"
        const val SEED_RATIO_LIMITED = "seedRatioLimited"
        const val SEED_RATIO_LIMIT = "seedRatioLimit"
        const val SEED_IDLE_LIMITED = "idle-seeding-limit-enabled"
        const val SEED_IDLE_LIMIT = "idle-seeding-limit"
        const val DOWNLOAD_DIR_FREE_SPACE = "download-dir-free-space"
        const val VERSION = "version"
    }
}
