package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;

public class ColorUtils {

    @ColorInt public static int resolveColor(Context context, int colorAttr, int defaultResId) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(colorAttr, typedValue, true);
        if (resolved) {
            if (typedValue.type == TypedValue.TYPE_STRING) {
                ColorStateList stateList = ContextCompat.getColorStateList(context, typedValue.resourceId);
                if (stateList != null) return stateList.getDefaultColor();
            } else {
                return typedValue.data;
            }
        }

        return ContextCompat.getColor(context, defaultResId);
    }
}
