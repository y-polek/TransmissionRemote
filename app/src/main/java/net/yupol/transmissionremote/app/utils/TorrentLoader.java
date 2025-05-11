package net.yupol.transmissionremote.app.utils;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TorrentLoader {

    public interface Callback {
        void onSuccess(byte[] data);
        void onError();
    }

    public static void loadTorrentFromUri(Uri fileUri, Callback callback) {
        if (fileUri == null || callback == null) {
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            byte[] bytes = null;
            try (InputStream inputStream = new URL(fileUri.toString()).openStream()) {
                bytes = IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                Log.e("TorrentLoader", "Failed to retrieve Uri: " + fileUri, e);
            }

            byte[] finalBytes = bytes;
            mainHandler.post(() -> {
                if (finalBytes != null) {
                    callback.onSuccess(finalBytes);
                } else {
                    callback.onError();
                }
            });
        });
    }
}