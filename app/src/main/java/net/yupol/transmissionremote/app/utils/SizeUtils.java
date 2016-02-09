package net.yupol.transmissionremote.app.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class SizeUtils {
    public static String displayableSize(long bytes) {
        double kbytes = bytes/1024.0;
        if (kbytes < 1000)
            return String.format("%.1f KB", kbytes);

        double mbytes = bytes/(1024.0*1024.0);
        if (mbytes < 1000)
            return String.format("%.1f MB", mbytes);

        double gbytes = bytes/(1024.0*1024.0*1024.0);
        return String.format("%.1f GB", gbytes);
    }

    public static float dpToPx(Resources resources, float dimenInDp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimenInDp, resources.getDisplayMetrics());
    }
}
