package net.yupol.transmissionremote.app.utils;

import android.app.UiModeManager;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    private static final String PREFERENCE_KEY_IN_NIGHT_MODE = "preference_key_in_night_mode";

    public static boolean isInNightMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCE_KEY_IN_NIGHT_MODE, false);
    }

    public static void setIsInNightMode(Context context, boolean isInNightMode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREFERENCE_KEY_IN_NIGHT_MODE, isInNightMode).apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            final UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            uiModeManager.setApplicationNightMode(isInNightMode ? UiModeManager.MODE_NIGHT_YES : UiModeManager.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(isInNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
