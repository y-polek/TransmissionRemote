package net.yupol.transmissionremote.app.preferences

import android.content.Context
import android.preference.PreferenceManager
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.preferences.delegates.StringToIntPreferenceDelegate

class Preferences(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val updateIntervalKey = context.getString(R.string.update_interval_key)
    private val updateIntervalDefaultValue = context.getString(R.string.update_interval_default_value).toInt()

    var updateInterval: Int by StringToIntPreferenceDelegate(prefs, updateIntervalKey, updateIntervalDefaultValue)
}
