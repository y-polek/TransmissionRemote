package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TransmissionRemotePreferenceManager {
    private static final String APPLICATION_PREFERENCES = "transmission_remote_app_prefs";

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
    }
}
