package net.yupol.transmissionremote.app

import org.junit.Test

import org.assertj.core.api.Java6Assertions.*

class InMemorySharedPreferencesTest {

    private val prefs = InMemorySharedPreferences()

    @Test
    fun testGetString() {
        prefs.edit().putString("name", "Jon Snow").apply()

        val readString = prefs.getString("name", null)

        assertThat(readString).isEqualTo("Jon Snow")
    }

    @Test
    fun testStringDefaultValue() {
        assertThat(prefs.getString("first name", null)).isNull()
        assertThat(prefs.getString("last name", "Doe")).isEqualTo("Doe")
    }

    @Test
    fun testGetInt() {
        prefs.edit().putInt("age", 32).apply()

        val readInt = prefs.getInt("age", -1)

        assertThat(readInt).isEqualTo(32)
    }

    @Test
    fun testIntDefaultValue() {
        assertThat(prefs.getInt("age", -1)).isEqualTo(-1)
        assertThat(prefs.getInt("year", 1984)).isEqualTo(1984)
    }

    @Test
    fun testGetLong() {
        prefs.edit().putLong("age", 32).apply()

        val readLong = prefs.getLong("age", -1)

        assertThat(readLong).isEqualTo(32)
    }

    @Test
    fun testLongDefaultValue() {
        assertThat(prefs.getLong("age", -1)).isEqualTo(-1)
        assertThat(prefs.getLong("year", 1984)).isEqualTo(1984)
    }

    @Test
    fun testGetFloat() {
        prefs.edit().putFloat("ratio", 0.3f).apply()

        val readFloat = prefs.getFloat("ratio", 0.0f)

        assertThat(readFloat).isEqualTo(0.3f)
    }

    @Test
    fun testFloatDefaultValue() {
        assertThat(prefs.getFloat("ratio", 1.1f)).isEqualTo(1.1f)
    }

    @Test
    fun testGetBoolean() {
        prefs.edit().putBoolean("enabled", true).apply()

        val readBoolean = prefs.getBoolean("enabled", false)

        assertThat(readBoolean).isTrue()
    }

    @Test
    fun testBooleanDefaultValue() {
        assertThat(prefs.getBoolean("enabled", true)).isTrue()
        assertThat(prefs.getBoolean("enabled", false)).isFalse()
    }

    @Test
    fun testGetStringSet() {
        prefs.edit().putStringSet("servers", setOf("Raspberry Pi", "Router", "Laptop")).apply()

        val strings = prefs.getStringSet("servers", null)

        assertThat(strings).containsExactlyInAnyOrder("Raspberry Pi", "Router", "Laptop")
    }

    @Test
    fun testStringSetDefaultValue() {
        assertThat(prefs.getStringSet("servers", null)).isNull()
        assertThat(prefs.getStringSet("servers", setOf())).isEmpty()
        assertThat(prefs.getStringSet("servers", setOf("Router", "Laptop")))
                .containsExactlyInAnyOrder("Router", "Laptop")
    }

    @Test
    fun testContains() {
        prefs.edit().putString("user", "Jon Snow").apply()

        assertThat(prefs.contains("user")).isTrue()
        assertThat(prefs.contains("server")).isFalse()
    }
}