package net.yupol.transmissionremote.app.preferences.delegates

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class StringToIntPreferenceDelegate(
        private val prefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Int)
{
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return prefs.getString(key, null)?.toIntOrNull() ?: defaultValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?) {
        prefs.edit {
            if (value == null) {
                remove(key)
            } else {
                putString(key, value.toString())
            }
        }
    }
}
