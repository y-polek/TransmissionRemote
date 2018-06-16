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

    @Test
    fun testEditMethodReturnsNewEditor() {
        assertThat(prefs.edit()).isNotEqualTo(prefs.edit())
    }

    @Test
    fun testPreferencesNotSavedIfCommitOrApplyNotCalled() {
        prefs.edit()
                .putString("string", "Jon Snow")
                .putBoolean("bool", true)
                .putInt("int", 42)
                .putLong("long", 43L)
                .putFloat("float", 4.3f)
                .putStringSet("string set", setOf("str"))

        assertThat(prefs.contains("string")).isFalse()
        assertThat(prefs.contains("bool")).isFalse()
        assertThat(prefs.contains("int")).isFalse()
        assertThat(prefs.contains("long")).isFalse()
        assertThat(prefs.contains("float")).isFalse()
        assertThat(prefs.contains("string set")).isFalse()
    }

    @Test
    fun testPreferenceSavesIfCommitCalled() {
        prefs.edit()
                .putString("string", "Jon Snow")
                .putBoolean("bool", true)
                .putInt("int", 42)
                .putLong("long", 43L)
                .putFloat("float", 4.3f)
                .putStringSet("string set", setOf("str"))
                .commit()

        assertThat(prefs.contains("string")).isTrue()
        assertThat(prefs.contains("bool")).isTrue()
        assertThat(prefs.contains("int")).isTrue()
        assertThat(prefs.contains("long")).isTrue()
        assertThat(prefs.contains("float")).isTrue()
        assertThat(prefs.contains("string set")).isTrue()
    }

    @Test
    fun testKeyNotRemovedIfCommitOrApplyNotCalled() {
        prefs.edit()
                .putString("user", "Jon Snow")
                .putInt("age", 42)
                .apply()

        prefs.edit().remove("user")

        assertThat(prefs.contains("user")).isTrue()
    }

    @Test
    fun testKeyRemovedWhenCommitCalled() {
        prefs.edit()
                .putString("first name", "Jon")
                .putString("last name", "Snow")
                .putInt("age", 42)
                .apply()

        prefs.edit()
                .remove("last name")
                .remove("age")
                .apply()

        assertThat(prefs.contains("first name")).isTrue()
        assertThat(prefs.contains("last name")).isFalse()
        assertThat(prefs.contains("age")).isFalse()
    }

    @Test
    fun testRemovedIsDoneFirst() {
        prefs.edit()
                .putString("first name", "Jon")
                .putString("last name", "Snow")
                .putInt("age", 42)
                .apply()

        prefs.edit()
                .putInt("age", 50)
                .remove("age")
                .apply()

        assertThat(prefs.getInt("age", 0)).isEqualTo(50)
    }

    @Test
    fun testKeysNotClearedIfCommitOrApplyNotCalled() {
        prefs.edit()
                .putString("name", "Jon")
                .putInt("age", 42)
                .apply()

        prefs.edit().clear()

        assertThat(prefs.contains("name")).isTrue()
        assertThat(prefs.contains("age")).isTrue()
    }

    @Test
    fun testKeysClearedIfCommitCalled() {
        prefs.edit()
                .putString("name", "Jon")
                .putInt("age", 42)
                .apply()

        prefs.edit().clear().commit()

        assertThat(prefs.contains("name")).isFalse()
        assertThat(prefs.contains("age")).isFalse()
    }

    @Test
    fun testClearIsDoneFirst() {
        prefs.edit()
                .putString("name", "Jon")
                .putInt("age", 42)
                .apply()

        prefs.edit()
                .putInt("age", 50)
                .clear()
                .apply()

        assertThat(prefs.contains("name")).isFalse()
        assertThat(prefs.getInt("age", 0)).isEqualTo(50)
    }
}