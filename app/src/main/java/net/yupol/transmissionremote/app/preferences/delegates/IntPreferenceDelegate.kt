package net.yupol.transmissionremote.app.preferences.delegates

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

class IntPreferenceDelegate(
        private val prefs: SharedPreferences,
        private val key: String,
        private val defaultValue: Int)
{
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return prefs.getInt(key, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?) {
        prefs.edit {
            if (value != null) {
                putInt(key, value)
            } else {
                remove(key)
            }
        }
    }
}
