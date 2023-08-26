package net.yupol.transmissionremote.app.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;

public class NotificationsPreferencesFragment extends PreferenceFragment {

    private String notificationChannelPreferenceKey;
    private Intent notificationChannelScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_preferences);
        setupNotificationChannelPreference();
    }

    private void setupNotificationChannelPreference() {
        notificationChannelPreferenceKey = getString(R.string.torrent_finished_notification_sound_and_vibrate_key);

        Preference pref = findPreference(notificationChannelPreferenceKey);
        if (pref != null) {
            pref.setTitle(
                    getString(R.string.torrent_finished_notification_sound_title) + "/" +
                    getString(R.string.torrent_finished_notification_vibrate_title));

            setupNotificationChannelIntent();
            pref.setEnabled(canOpenNotificationChannelScreen());
        }
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

    private void openNotificationChannelSettings() {
        startActivity(notificationChannelScreen);
    }

    private void setupNotificationChannelIntent() {
        notificationChannelScreen = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        notificationChannelScreen.putExtra(Settings.EXTRA_CHANNEL_ID, TransmissionRemote.NOTIFICATION_CHANNEL_ID);
        notificationChannelScreen.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
    }

    private boolean canOpenNotificationChannelScreen() {
        return notificationChannelScreen.resolveActivity(getActivity().getPackageManager()) != null;
    }
}
