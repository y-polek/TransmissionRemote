package net.yupol.transmissionremote.app.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    private static final String PREFERENCE_KEY_IN_NIGHT_MODE = "preference_key_in_night_mode";

    public static boolean isInNightMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCE_KEY_IN_NIGHT_MODE, false);
    }

    public static void setIsInNightMode(Context context, boolean isInNightMode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREFERENCE_KEY_IN_NIGHT_MODE, isInNightMode).apply();
        AppCompatDelegate.setDefaultNightMode(isInNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
