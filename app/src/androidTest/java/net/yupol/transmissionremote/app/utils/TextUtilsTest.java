package net.yupol.transmissionremote.app.utils;

import junit.framework.TestCase;

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
}
