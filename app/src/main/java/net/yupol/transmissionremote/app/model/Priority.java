package net.yupol.transmissionremote.app.model;

import androidx.annotation.StringRes;

import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial;

import net.yupol.transmissionremote.app.R;

public enum Priority {

    HIGH(1, R.string.priority_high, CommunityMaterial.Icon.cmd_chevron_up),
    NORMAL(0, R.string.priority_normal, CommunityMaterial.Icon2.cmd_minus),
    LOW(-1, R.string.priority_low, CommunityMaterial.Icon.cmd_chevron_down);

    public final int value;
    @StringRes public final int nameResId;
    public final IIcon icon;

    Priority(int value, @StringRes int nameResId, IIcon icon) {
        this.value = value;
        this.nameResId = nameResId;
        this.icon = icon;
    }

    public static Priority fromValue(int value, Priority defaultPriority) {
        for (Priority priority : values()) {
            if (priority.value == value) return priority;
        }
        return defaultPriority;
    }
}
