package net.yupol.transmissionremote.app.model;

import androidx.annotation.StringRes;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import net.yupol.transmissionremote.app.R;

public enum Priority {

    HIGH(1, R.string.priority_high, FontAwesome.Icon.faw_angle_up),
    NORMAL(0, R.string.priority_normal, CommunityMaterial.Icon2.cmd_minus),
    LOW(-1, R.string.priority_low, FontAwesome.Icon.faw_angle_down);

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
