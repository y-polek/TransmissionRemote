package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcSession(
    @SerialName("speed-limit-down") var speedLimitDown: Int = 100,
    @SerialName("speed-limit-down-enabled") var isSpeedLimitDownEnabled: Boolean = false,
    @SerialName("speed-limit-up") var speedLimitUp: Int = 50,
    @SerialName("speed-limit-up-enabled") var isSpeedLimitUpEnabled: Boolean = false,
    @SerialName("alt-speed-down") var altSpeedLimitDown: Int = 512,
    @SerialName("alt-speed-up") var altSpeedLimitUp: Int = 0,
    @SerialName("alt-speed-enabled") var isAltSpeedLimitEnabled: Boolean = false,
    @SerialName("download-dir") var downloadDir: String = "~/Downloads",
    @SerialName("seedRatioLimited") var isSeedRatioLimited: Boolean = false,
    @SerialName("seedRatioLimit") var seedRatioLimit: Double = 2.0,
    @SerialName("idle-seeding-limit-enabled") var isSeedIdleLimited: Boolean = false,
    @SerialName("idle-seeding-limit") var seedIdleLimit: Int = 30,
    @SerialName("download-dir-free-space") var downloadDirFreeSpace: Long = 77022388224,
    @SerialName("version") val version: String = "4.0.4 (24077e3511)"
) : RpcArguments
