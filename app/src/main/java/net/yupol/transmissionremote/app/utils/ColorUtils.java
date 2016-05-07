package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;

public class ColorUtils {

    public static int resolveColor(Context context, int colorAttr, int defaultResId) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(colorAttr, typedValue, true);
        if (resolved) {
            if (typedValue.type == TypedValue.TYPE_STRING) {
                ColorStateList stateList = context.getResources().getColorStateList(typedValue.resourceId);
                if (stateList != null) return stateList.getDefaultColor();
            } else {
                return typedValue.data;
            }
        }

        return context.getResources().getColor(defaultResId);
    }
}
