package net.yupol.transmissionremote.app.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;

public class NotificationsPreferencesFragment extends PreferenceFragment {

    private String notificationChannelPreferenceKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);

        notificationChannelPreferenceKey = getString(R.string.torrent_finished_notification_sound_and_vibrate_key);

        Preference notificationChannelPreference = findPreference(notificationChannelPreferenceKey);
        notificationChannelPreference.setTitle(
                getString(R.string.torrent_finished_notification_sound_title) + "/" +
                getString(R.string.torrent_finished_notification_vibrate_title));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (notificationChannelPreferenceKey.equals(preference.getKey())) {
            openNotificationChannelSettings();
            return true;
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void openNotificationChannelSettings() {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, TransmissionRemote.NOTIFICATION_CHANNEL_ID);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
        startActivity(intent);
    }
}
