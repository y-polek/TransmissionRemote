package net.yupol.transmissionremote.app.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.YEAR;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TextUtils {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

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

        StringBuilder b = new StringBuilder();
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

    public static String displayableDate(long timestampSeconds) {
        long timestampMillis = timestampSeconds * 1000;
        Date date = new Date(timestampMillis);
        String formattedTime = TIME_FORMAT.format(date);

        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeInMillis(timestampMillis);
        int year = timestamp.get(YEAR);
        int day = timestamp.get(DAY_OF_YEAR);
        Calendar today = Calendar.getInstance();
        if (year == today.get(YEAR) && day == today.get(DAY_OF_YEAR)) {
            return TransmissionRemote.getInstance().getString(R.string.today_time, formattedTime);
        }

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        if (year == yesterday.get(YEAR) && day == yesterday.get(DAY_OF_YEAR)) {
            return TransmissionRemote.getInstance().getString(R.string.yesterday_time, formattedTime);
        }

        return TransmissionRemote.getInstance().getString(R.string.date_time, DATE_FORMAT.format(date), formattedTime);
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
