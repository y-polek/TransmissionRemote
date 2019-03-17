package net.yupol.transmissionremote.app.preferences

import android.app.Application
import android.preference.PreferenceManager
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.preferences.delegates.BooleanPreferenceDelegate
import net.yupol.transmissionremote.app.preferences.delegates.StringPreferenceDelegate
import net.yupol.transmissionremote.app.preferences.delegates.StringToIntPreferenceDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(app: Application) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    private val updateIntervalKey = app.getString(R.string.update_interval_key)
    private val updateIntervalDefaultValue = app.getString(R.string.update_interval_default_value).toInt()

    var updateInterval: Int by StringToIntPreferenceDelegate(prefs, updateIntervalKey, updateIntervalDefaultValue)

    var storagePermissionDeniedBefore: Boolean by BooleanPreferenceDelegate(prefs, KEY_STORAGE_PERMISSION_DENIED_BEFORE, false)

    var lastUsedTorrentFileDirectory: String? by StringPreferenceDelegate(prefs, KEY_LAST_USED_TORRENT_FILE_DIRECTORY)

    companion object {
        private const val KEY_STORAGE_PERMISSION_DENIED_BEFORE = "key_storage_permission_denied_before"
        private const val KEY_LAST_USED_TORRENT_FILE_DIRECTORY = "key_last_used_torrent_file_directory"
    }
}
