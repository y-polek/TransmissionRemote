package net.yupol.transmissionremote.app

import android.content.SharedPreferences

class InMemorySharedPreferences: SharedPreferences {

    private val map: Map<String, Any>
    private val editor: Editor

    init {
        val storage = mutableMapOf<String, Any>()
        map = storage
        editor = Editor(storage)
    }

    override fun edit() = editor

    override fun getString(key: String, defValue: String?): String? {
        return (map[key] ?: defValue) as String?
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return (map[key] ?: defValue) as Boolean
    }

    override fun getInt(key: String, defValue: Int): Int {
        return (map[key] ?: defValue) as Int
    }

    override fun getLong(key: String, defValue: Long): Long {
        return (map[key] ?: defValue) as Long
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return (map[key] ?: defValue) as Float
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        return (map[key] ?: defValues) as Set<String>?
    }

    override fun contains(key: String) = key in map

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

        override fun putString(key: String, value: String): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putStringSet(key: String, values: Set<String>): SharedPreferences.Editor {
            map[key] = values
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            map.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            map.clear()
            return this
        }

        override fun commit(): Boolean = true

        override fun apply() {}
    }
}