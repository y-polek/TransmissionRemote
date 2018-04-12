package net.yupol.transmissionremote.model.json;

public enum TransferPriority {
    HIGH(1),
    NORMAL(0),
    LOW(-1);

    private int modelValue;

    TransferPriority(int modelValue) {
        this.modelValue = modelValue;
    }

    public int getModelValue() {
        return modelValue;
    }

    public static TransferPriority fromModelValue(int value) {
        if (value == 0) return NORMAL;
        if (value < 0) return LOW;
        return HIGH;
    }
}
