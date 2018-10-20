package net.yupol.transmissionremote.app.preferences.delegates

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class BooleanPreferenceDelegate(
        private val prefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Boolean)
{
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) {
        prefs.edit {
            if (value != null) {
                putBoolean(key, value)
            } else {
                remove(key)
            }
        }
    }
}
