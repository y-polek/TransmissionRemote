package net.yupol.transmissionremote.app

import android.content.SharedPreferences

class InMemorySharedPreferences: SharedPreferences {

    private val storage = mutableMapOf<String, Any>()

    override fun edit() = Editor(storage)

    override fun getString(key: String, defValue: String?): String? {
        return (storage[key] ?: defValue) as String?
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return (storage[key] ?: defValue) as Boolean
    }

    override fun getInt(key: String, defValue: Int): Int {
        return (storage[key] ?: defValue) as Int
    }

    override fun getLong(key: String, defValue: Long): Long {
        return (storage[key] ?: defValue) as Long
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return (storage[key] ?: defValue) as Float
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        return (storage[key] ?: defValues) as Set<String>?
    }

    override fun contains(key: String) = key in storage

    override fun getAll(): MutableMap<String, *> {
        throw NotImplementedError()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw NotImplementedError()
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        throw NotImplementedError()
    }

    class Editor(private val map: MutableMap<String, Any>): SharedPreferences.Editor {

        private val additions = mutableMapOf<String, Any>()
        private val removals = mutableSetOf<String>()
        private var clear = false

        override fun putString(key: String, value: String): SharedPreferences.Editor {
            additions[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            additions[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            additions[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            additions[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            additions[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>): SharedPreferences.Editor {
            additions[key] = values
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            removals += key
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            clear = true
            return this
        }

        override fun commit(): Boolean {
            if (clear) map.clear()

            removals.forEach {
                map -= it
            }

            additions.entries.forEach {
                map[it.key] = it.value
            }

            return true
        }

        override fun apply() {
            commit()
        }
    }
}