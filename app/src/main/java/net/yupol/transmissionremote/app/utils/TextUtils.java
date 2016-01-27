package net.yupol.transmissionremote.app.utils;

public class TextUtils {

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
}
