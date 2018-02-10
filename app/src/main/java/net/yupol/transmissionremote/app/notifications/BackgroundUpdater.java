package net.yupol.transmissionremote.app.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BackgroundUpdater {

    public static void start(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(new Intent(context, BackgroundUpdateService.class));
        } else {
            BackgroundUpdateJob.schedule();
        }
    }

    public static void stop(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.stopService(new Intent(context, BackgroundUpdateService.class));
        } else {
            BackgroundUpdateJob.cancelAll();
        }
    }
}
