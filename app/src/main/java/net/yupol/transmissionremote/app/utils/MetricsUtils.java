package net.yupol.transmissionremote.app.utils;

import android.content.Context;

public class MetricsUtils {

    public static float px2dp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float dp2px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
