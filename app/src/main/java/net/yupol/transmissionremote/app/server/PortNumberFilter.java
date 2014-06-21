package net.yupol.transmissionremote.app.server;

import android.text.InputFilter;
import android.text.Spanned;

public class PortNumberFilter implements InputFilter {

    private static PortNumberFilter instance;

    public static PortNumberFilter instance() {
        if (instance == null) {
            synchronized (PortNumberFilter.class) {
                if (instance == null) {
                    instance = new PortNumberFilter();
                }
            }
        }
        return instance;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.length() == 0)
            return null;
        String result = resultingText(source, start, end, dest, dstart, dend);
        if (result.startsWith("0"))
            return "";
        try {
            Integer port = Integer.parseInt(result);
            if (port > 0xFFFF)
                return dest.subSequence(dstart, dend);
        } catch (NumberFormatException e) {
            return "";
        }
        return null;
    }

    private String resultingText(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder b = new StringBuilder();
        b.append(dest.subSequence(0, dstart));
        b.append(source.subSequence(start, end));
        b.append(dest.subSequence(dend, dest.length()));
        return b.toString();
    }
}
