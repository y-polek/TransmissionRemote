package net.yupol.transmissionremote.app.preferences

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.preferences.delegates.IntPreferenceDelegate

class Preferences(context: Context) {

    companion object {
        private const val UPDATE_INTERVAL = "update_interval"
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    init {
        migratePreferences()
    }

    private val updateIntervalKey = context.getString(R.string.update_interval_key)
    private val updateIntervalDefaultValue = context.resources.getInteger(R.integer.update_interval_default_value)

    var updateInterval: Int by IntPreferenceDelegate(prefs, updateIntervalKey, updateIntervalDefaultValue)

    private fun migratePreferences() {
        migrateUpdateIntervalPreference()
    }

    private fun migrateUpdateIntervalPreference() {
        if (prefs.contains(updateIntervalKey)) {
            val value = prefs.getString(UPDATE_INTERVAL, null).toIntOrNull()
            prefs.edit {
                remove(UPDATE_INTERVAL)
                if (value != null) putInt(UPDATE_INTERVAL, value)
            }
        }
    }
}
