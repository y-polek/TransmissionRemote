package net.yupol.transmissionremote.app.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.yupol.transmissionremote.app.R;

public enum Priority {

    HIGH(1, R.string.priority_high, R.drawable.ic_priority_high),
    NORMAL(0, R.string.priority_normal, R.drawable.ic_priority_normal),
    LOW(-1, R.string.priority_low, R.drawable.ic_priority_low);

    public final int value;
    @StringRes public final int nameResId;
    @DrawableRes public final int iconRes;

    Priority(int value, @StringRes int nameResId, @DrawableRes int iconRes) {
        this.value = value;
        this.nameResId = nameResId;
        this.iconRes = iconRes;
    }

    public static Priority fromValue(int value, Priority defaultPriority) {
        for (Priority priority : values()) {
            if (priority.value == value) return priority;
        }
        return defaultPriority;
    }
}
