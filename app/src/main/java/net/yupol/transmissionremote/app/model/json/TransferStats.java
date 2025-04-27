package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

public class TransferStats {

    @Key private long downloadedBytes;
    @Key private long secondActive;
    @Key private long uploadedBytes;

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getUploadedBytes() {
        return uploadedBytes;
    }

    public long getSecondActive() {
        return secondActive;
    }
}
