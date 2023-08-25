package net.yupol.transmissionremote.app.utils;


import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;

public class ParcelableUtils {

    @Nullable
    public static <T> T[] toArrayOfType(Class<T> type, @Nullable Parcelable[] original) {
        if (original == null) return null;
        T[] newArray = (T[]) Array.newInstance(type, original.length);
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }
}
