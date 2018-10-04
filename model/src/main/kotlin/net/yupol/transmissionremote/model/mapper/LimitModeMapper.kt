package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.domain.model.IdleLimitMode
import net.yupol.transmissionremote.domain.model.RatioLimitMode
import net.yupol.transmissionremote.model.limitmode.LimitMode

object LimitModeMapper {

    @JvmStatic
    fun toRatioLimitMode(mode: LimitMode): RatioLimitMode {
        return when (mode.value) {
            0 -> RatioLimitMode.GLOBAL_SETTINGS
            1 -> RatioLimitMode.STOP_AT_RATIO
            2 -> RatioLimitMode.UNLIMITED
            else -> throw RuntimeException("Unknown Limit Mode code: ${mode.value}")
        }
    }

    @JvmStatic
    fun toIdleLimitMode(mode: LimitMode): IdleLimitMode {
        return when (mode.value) {
            0 -> IdleLimitMode.GLOBAL_SETTINGS
            1 -> IdleLimitMode.STOP_WHEN_INACTIVE
            2 -> IdleLimitMode.UNLIMITED
            else -> throw RuntimeException("Unknown Limit Mode code: ${mode.value}")
        }
    }
}