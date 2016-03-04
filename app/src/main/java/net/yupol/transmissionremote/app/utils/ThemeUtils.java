package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.util.TypedValue;

public class ThemeUtils {

    public static int resolveColor(Context context, int colorAttr, int defaultResId) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(colorAttr, typedValue, true);
        if (resolved) {
            return typedValue.data;
        } else {
            return context.getResources().getColor(defaultResId);
        }
    }
}
