package net.yupol.transmissionremote.app.utils;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;

import java.util.Map;

public class TorrentUtils {

    private static final String INFO = "info";
    private static final String NAME = "name";
    private static final Bencode ENCODER = new Bencode();

    public static String readName(byte[] torrentFileContent) {
        Map info = (Map) ENCODER.decode(torrentFileContent, Type.DICTIONARY).get(INFO);
        if (info != null) {
            return (String) info.get(NAME);
        }
        return null;
    }
}
