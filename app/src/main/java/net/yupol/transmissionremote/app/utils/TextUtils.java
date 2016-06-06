package net.yupol.transmissionremote.app.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TextUtils {

    private static final long ETA_INFINITE_THRESHOLD = DAYS.toSeconds(7);

    public static String abbreviate(String text) {
        String[] words = text.split("\\s");

        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (i < words.length && builder.length() < 2) {
            String word = words[i].trim();
            if (word.length() > 0) builder.append(Character.toUpperCase(word.charAt(0)));
            i++;
        }

        return builder.toString();
    }

    public static String displayableSize(long bytes) {
        double kbytes = bytes/1024.0;
        if (kbytes < 1000)
            return String.format("%.1f KB", kbytes);

        double mbytes = bytes/(1024.0*1024.0);
        if (mbytes < 1000)
            return String.format("%.1f MB", mbytes);

        double gbytes = bytes/(1024.0*1024.0*1024.0);
        return String.format("%.1f GB", gbytes);
    }

    public static String displayableTime(final long timeInSeconds) {
        long days = SECONDS.toDays(timeInSeconds);
        long hours = SECONDS.toHours(timeInSeconds) % 24;
        long minutes = SECONDS.toMinutes(timeInSeconds) % 60;
        long seconds = timeInSeconds % 60;

        StringBuilder b = new StringBuilder("~ ");
        if (days > 0) {
            b.append(days).append("d ");
            b.append(hours).append('h');
        } else if (hours > 0) {
            b.append(hours).append("h ");
            b.append(minutes).append('m');
        } else {
            if (minutes > 0) b.append(minutes).append("m ");
            b.append(seconds).append('s');
        }

        return b.toString();
    }



    private static void clickify(TextView view, int start, int end,
                                 final View.OnClickListener listener) {

        end = start + (int) (Math.random() * (end - start));


        SpannableString s = SpannableString.valueOf(view.getText() + String.valueOf((int) (Math.random() * 1000)));
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                listener.onClick(widget);
            }
        };
        s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(s);

        //view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void clickify(TextView view, String clickableText,
                                View.OnClickListener listener) {

        String string = view.getText().toString();
        int start = string.indexOf(clickableText);
        if (start == -1) return;
        int end = start + clickableText.length();

        clickify(view, start, end, listener);
    }

    public static void clickify(TextView view, View.OnClickListener listener) {
        clickify(view, 0, view.getText().length(), listener);
    }

    private static class ClickSpan extends ClickableSpan {

        private View.OnClickListener mListener;

        public ClickSpan(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View widget) {
            if (mListener != null) mListener.onClick(widget);
        }
    }
}
