package net.yupol.transmissionremote.app.preferences

import android.app.Application
import android.preference.PreferenceManager
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.preferences.delegates.StringToIntPreferenceDelegate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(app: Application) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    private val updateIntervalKey = app.getString(R.string.update_interval_key)
    private val updateIntervalDefaultValue = app.getString(R.string.update_interval_default_value).toInt()

    var updateInterval: Int by StringToIntPreferenceDelegate(prefs, updateIntervalKey, updateIntervalDefaultValue)
}
