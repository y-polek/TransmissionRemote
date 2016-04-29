package net.yupol.transmissionremote.app.utils;

import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;

public class TextUtilsTest extends TestCase {

    public void testAbbreviateSingleWord() {
        assertEquals("A", TextUtils.abbreviate("Abc"));
    }

    public void testAbbreviateTwoWords() {
        assertEquals("AB", TextUtils.abbreviate("Abc Bcd"));
    }

    public void testAbbreviateThreeWords() {
        assertEquals("AB", TextUtils.abbreviate("Abc Bcd Cde"));
    }

    public void testAbbreviateEmptyString() {
        assertEquals("", TextUtils.abbreviate(""));
    }

    public void testAbbreviateWhitespacesOnly() {
        assertEquals("", TextUtils.abbreviate(" \t \t"));
    }

    public void testAbbreviateIsCapitalized() {
        assertEquals("AB", TextUtils.abbreviate("abc bcd"));
    }

    public void testAbbreviateStartsWithWhitespace() {
        assertEquals("AB", TextUtils.abbreviate(" Abc Bcd"));
    }

    public void testAbbreviateTabSeparated() {
        assertEquals("AB", TextUtils.abbreviate("Abc\tBcd"));
    }

    public void testAbbreviateLongWhitespace() {
        assertEquals("AB", TextUtils.abbreviate("Abc    \tBcd"));
    }

    public void testDisplayableSize() {
        assertEquals("0.0 KB", TextUtils.displayableSize(0));
        assertEquals("0.0 KB", TextUtils.displayableSize(1));
        assertEquals("0.1 KB", TextUtils.displayableSize(100));
        assertEquals("1.0 KB", TextUtils.displayableSize(1000));
        assertEquals("1.0 KB", TextUtils.displayableSize(1024));
        assertEquals("1.0 KB", TextUtils.displayableSize(1025));
        long KB = 1024;
        assertEquals("2.0 KB", TextUtils.displayableSize(2 * 1024));
        assertEquals("999.0 KB", TextUtils.displayableSize(999 * KB));
        assertEquals("1.0 MB", TextUtils.displayableSize(1000 * KB));
        assertEquals("1.0 MB", TextUtils.displayableSize(1024 * KB));
        assertEquals("1.0 MB", TextUtils.displayableSize(1025 * KB));
        long MB = 1024 * KB;
        assertEquals("2.0 MB", TextUtils.displayableSize(2 * MB));
        assertEquals("100.0 MB", TextUtils.displayableSize(100 * MB));
        assertEquals("247.0 MB", TextUtils.displayableSize(247 * MB));
        long GB = 1024 * MB;
        assertEquals("1.0 GB", TextUtils.displayableSize(1000 * MB));
        assertEquals("1.0 GB", TextUtils.displayableSize(1024 * MB));
        assertEquals("12345.0 GB", TextUtils.displayableSize(12345 * GB));
    }

    public void testDisplayableTime() {
        assertEquals("~ 0s", TextUtils.displayableTime(0));
        assertEquals("~ 10s", TextUtils.displayableTime(10));
        assertEquals("~ 1m 1s", TextUtils.displayableTime(61));
        assertEquals("~ 1h 0m", TextUtils.displayableTime(TimeUnit.HOURS.toSeconds(1)));
        assertEquals("~ 19h 59m", TextUtils.displayableTime(TimeUnit.HOURS.toSeconds(19) + TimeUnit.MINUTES.toSeconds(59) + 59));
        assertEquals("~ 15d 0h", TextUtils.displayableTime(TimeUnit.DAYS.toSeconds(15) + 3));
    }
}
