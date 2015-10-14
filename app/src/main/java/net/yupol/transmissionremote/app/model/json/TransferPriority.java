package net.yupol.transmissionremote.app.model.json;

import net.yupol.transmissionremote.app.R;

public enum TransferPriority {
    HIGH(1, R.string.priority_high, R.drawable.priority_high),
    NORMAL(0, R.string.priority_normal, R.drawable.priority_normal),
    LOW(-1, R.string.priority_low, R.drawable.priority_low);

    private int modelValue;
    private int textRes;
    private int imageRes;

    TransferPriority(int modelValue, int textRes, int imageRes) {
        this.modelValue = modelValue;
        this.textRes = textRes;
        this.imageRes = imageRes;
    }

    public int getModelValue() {
        return modelValue;
    }

    public int getTextRes() {
        return textRes;
    }

    public int getImageRes() {
        return imageRes;
    }

    public static TransferPriority fromModelValue(int value) {
        if (value == 0) return NORMAL;
        if (value < 0) return LOW;
        return HIGH;
    }
}
