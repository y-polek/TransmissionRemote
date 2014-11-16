package net.yupol.transmissionremote.app.tests;

import junit.framework.TestCase;

import net.yupol.transmissionremote.app.utils.SizeUtils;

public class SizeUtilsTest extends TestCase {

    public void test() {
        assertEquals("0.0 KB", SizeUtils.displayableSize(0));
        assertEquals("0.0 KB", SizeUtils.displayableSize(1));
        assertEquals("0.1 KB", SizeUtils.displayableSize(100));
        assertEquals("1.0 KB", SizeUtils.displayableSize(1000));
        assertEquals("1.0 KB", SizeUtils.displayableSize(1024));
        assertEquals("1.0 KB", SizeUtils.displayableSize(1025));
        long KB = 1024;
        assertEquals("2.0 KB", SizeUtils.displayableSize(2 * 1024));
        assertEquals("999.0 KB", SizeUtils.displayableSize(999 * KB));
        assertEquals("1.0 MB", SizeUtils.displayableSize(1000 * KB));
        assertEquals("1.0 MB", SizeUtils.displayableSize(1024 * KB));
        assertEquals("1.0 MB", SizeUtils.displayableSize(1025 * KB));
        long MB = 1024 * KB;
        assertEquals("2.0 MB", SizeUtils.displayableSize(2 * MB));
        assertEquals("100.0 MB", SizeUtils.displayableSize(100 * MB));
        assertEquals("247.0 MB", SizeUtils.displayableSize(247 * MB));
        long GB = 1024 * MB;
        assertEquals("1.0 GB", SizeUtils.displayableSize(1000 * MB));
        assertEquals("1.0 GB", SizeUtils.displayableSize(1024 * MB));
        assertEquals("12345.0 GB", SizeUtils.displayableSize(12345 * GB));
    }
}
