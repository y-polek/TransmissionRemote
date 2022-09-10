package net.yupol.transmissionremote.app.utils;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TextUtilsTest {

    @Test
    public void testAbbreviateSingleWord() {
        assertThat(TextUtils.abbreviate("Abc")).isEqualTo("A");
    }

    @Test
    public void testAbbreviateTwoWords() {
        assertThat(TextUtils.abbreviate("Abc Bcd")).isEqualTo("AB");
    }

    @Test
    public void testAbbreviateThreeWords() {
        assertThat(TextUtils.abbreviate("Abc Bcd Cde")).isEqualTo("AB");
    }

    @Test
    public void testAbbreviateEmptyString() {
        assertThat(TextUtils.abbreviate("")).isEmpty();
    }

    @Test
    public void testAbbreviateWhitespacesOnly() {
        assertThat(TextUtils.abbreviate(" \t \t")).isEmpty();
    }

    @Test
    public void testAbbreviateIsCapitalized() {
        assertThat(TextUtils.abbreviate("abc bcd")).isEqualTo("AB");
    }

    @Test
    public void testAbbreviateStartsWithWhitespace() {
        assertThat(TextUtils.abbreviate(" Abc Bcd")).isEqualTo("AB");
    }

    @Test
    public void testAbbreviateTabSeparated() {
        assertThat(TextUtils.abbreviate("Abc\tBcd")).isEqualTo("AB");
    }

    @Test
    public void testAbbreviateLongWhitespace() {
        assertThat(TextUtils.abbreviate("Abc    \tBcd")).isEqualTo("AB");
    }

    @Test
    public void testDisplayableSize() {
        assertThat(TextUtils.displayableSize(0)).isEqualTo("0.0 KB");
        assertThat(TextUtils.displayableSize(1)).isEqualTo("0.0 KB");
        assertThat(TextUtils.displayableSize(100)).isEqualTo("0.1 KB");
        assertThat(TextUtils.displayableSize(1000)).isEqualTo("1.0 KB");
        assertThat(TextUtils.displayableSize(1024)).isEqualTo("1.0 KB");
        assertThat(TextUtils.displayableSize(1025)).isEqualTo("1.0 KB");
        long KB = 1024;
        assertThat(TextUtils.displayableSize(2 * 1024)).isEqualTo("2.0 KB");
        assertThat(TextUtils.displayableSize(999 * KB)).isEqualTo("999.0 KB");
        assertThat(TextUtils.displayableSize(1000 * KB)).isEqualTo("1.0 MB");
        assertThat(TextUtils.displayableSize(1024 * KB)).isEqualTo("1.0 MB");
        assertThat(TextUtils.displayableSize(1025 * KB)).isEqualTo("1.0 MB");
        long MB = 1024 * KB;
        assertThat(TextUtils.displayableSize(2 * MB)).isEqualTo("2.0 MB");
        assertThat(TextUtils.displayableSize(100 * MB)).isEqualTo("100.0 MB");
        assertThat(TextUtils.displayableSize(247 * MB)).isEqualTo("247.0 MB");
        long GB = 1024 * MB;
        assertThat(TextUtils.displayableSize(1000 * MB)).isEqualTo("1.0 GB");
        assertThat(TextUtils.displayableSize(1024 * MB)).isEqualTo("1.0 GB");
        assertThat(TextUtils.displayableSize(12345 * GB)).isEqualTo("12345.0 GB");
    }

    @Test
    public void testDisplayableTime() {
        assertThat(TextUtils.displayableTime(0)).isEqualTo("0s");
        assertThat(TextUtils.displayableTime(10)).isEqualTo("10s");
        assertThat(TextUtils.displayableTime(60)).isEqualTo("1m 0s");
        assertThat(TextUtils.displayableTime(61)).isEqualTo("1m 1s");
        assertThat(TextUtils.displayableTime(TimeUnit.HOURS.toSeconds(1))).isEqualTo("1h 0m");
        assertThat(TextUtils.displayableTime(TimeUnit.HOURS.toSeconds(19) + TimeUnit.MINUTES.toSeconds(59) + 59)).isEqualTo("19h 59m");
        assertThat(TextUtils.displayableTime(TimeUnit.DAYS.toSeconds(15) + 3)).isEqualTo("15d 0h");
    }
}
