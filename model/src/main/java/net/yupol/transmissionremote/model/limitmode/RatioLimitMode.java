package net.yupol.transmissionremote.model.limitmode;

import net.yupol.transmissionremote.model.R;

public enum RatioLimitMode implements LimitMode {
    GLOBAL_SETTINGS(0, R.string.global_settings),
    STOP_AT_RATIO(1, R.string.stop_at_ratio),
    UNLIMITED(2, R.string.unlimited);

    private int value;
    private int textRes;

    RatioLimitMode(int value, int textRes) {
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

    public static RatioLimitMode fromValue(int value) {
        for (RatioLimitMode mode : values()) {
            if (mode.value == value)
                return mode;
        }
        return null;
    }
}
