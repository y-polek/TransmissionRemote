package net.yupol.transmissionremote.app.utils;

import java.text.DateFormat;
import java.util.Date;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TextUtils {

    private static final long ETA_INFINITE_THRESHOLD = DAYS.toSeconds(7);
    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

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

    public static String displayableDate(long timestampSeconds) {
        return DATE_FORMAT.format(new Date(timestampSeconds * 1000));
    }
}
