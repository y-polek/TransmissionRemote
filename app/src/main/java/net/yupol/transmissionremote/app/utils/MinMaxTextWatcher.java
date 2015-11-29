package net.yupol.transmissionremote.app.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class MinMaxTextWatcher implements TextWatcher {

    private static final String TAG = MinMaxTextWatcher.class.getSimpleName();

    private int min, max;
    private String minStr, maxStr;
    private boolean selfChange = false;

    public MinMaxTextWatcher(int min, int max) {
        this.min = min;
        this.max = max;
        minStr = String.valueOf(min);
        maxStr = String.valueOf(max);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (selfChange) return;
        selfChange = true;
        try {
            int value = Integer.parseInt(s.toString());
            if(value < min) {
                s.replace(0, s.length(), minStr, 0, minStr.length());
            } else if(value > max) {
                s.replace(0, s.length(), maxStr, 0, maxStr.length());
            }
        } catch (NumberFormatException ex) {
            Log.e(TAG, "Can't parse number '" + s + "'");
        } finally {
            selfChange = false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
