package net.yupol.transmissionremote.app.notifications;

import android.content.Context;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.utils.TransmissionRemotePreferenceManager;

public class BackgroundUpdater {

    public static void start(Context context) {
        boolean onlyUnmeteredNetwork = TransmissionRemotePreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.background_update_only_unmetered_wifi_key), true);
        BackgroundUpdateJob.schedule(onlyUnmeteredNetwork);
    }

    public static void stop(Context context) {
        BackgroundUpdateJob.cancelAll();
    }

    public static void restart(Context context) {
        stop(context);
        start(context);
    }
}
