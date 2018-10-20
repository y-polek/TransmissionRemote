package net.yupol.transmissionremote.app.preferences.delegates

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class StringPreferenceDelegate(
        private val prefs: SharedPreferences,
        private val key: String)
{
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return prefs.getString(key, null)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        prefs.edit { putString(key, value) }
    }
}
