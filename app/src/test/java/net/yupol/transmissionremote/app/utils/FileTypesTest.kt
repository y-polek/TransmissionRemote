package net.yupol.transmissionremote.app.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class FileTypesTest {

    @Test
    fun testExtension() {
        assertThat("abc.txt".extension()).isEqualTo("txt")
        assertThat("/path/file.jpg".extension()).isEqualTo("jpg")
    }

    @Test
    fun testEmptyExtension() {
        assertThat("/abc/efd".extension()).isEmpty()
    }

    @Test
    fun testEmptyName() {
        assertThat(".gitignore".extension()).isEmpty()
    }

    @Test
    fun testMultipleDots() {
        assertThat("file.tar.gz".extension()).isEqualTo("gz")
    }
}
