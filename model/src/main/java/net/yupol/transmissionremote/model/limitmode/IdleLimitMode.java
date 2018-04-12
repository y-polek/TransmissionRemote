package net.yupol.transmissionremote.model.limitmode;

import net.yupol.transmissionremote.model.R;

public enum IdleLimitMode implements LimitMode {
    GLOBAL_SETTINGS(0, R.string.global_settings),
    STOP_WHEN_INACTIVE(1, R.string.stop_when_inactive),
    UNLIMITED(2, R.string.unlimited);

    private int value;
    private int textRes;

    IdleLimitMode(int value, int textRes) {
        this.value = value;
        this.textRes = textRes;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int getTextRes() {
        return textRes;
    }

    public static IdleLimitMode fromValue(int value) {
        for (IdleLimitMode mode : values()) {
            if (mode.value == value)
                return mode;
        }
        return null;
    }
}
