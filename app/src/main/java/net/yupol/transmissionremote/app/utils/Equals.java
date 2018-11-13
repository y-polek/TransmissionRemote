package net.yupol.transmissionremote.app.utils;

public class Equals {

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
