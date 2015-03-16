package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

public class Torrent {
    @Key private int id;
    @Key private String name;
    @Key private long addedData;
    @Key private long totalSize;
    @Key private double percentDone;
    @Key private int status;
    @Key("rateDownload") private int downloadRate;
    @Key("rateUpload") private int uploadRate;
    @Key private int leftUntilDone;
    @Key("uploadedEver") private long uploadedSize;
    @Key private double uploadRatio;
    @Key("error") private int errorId;
    private Error error;
    @Key private String errorString;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getAddedData() {
        return addedData;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public double getPercentDone() {
        return percentDone;
    }

    public Status getStatus() {
        return Status.fromValue(status);
    }

    public int getDownloadRate() {
        return downloadRate;
    }

    public int getUploadRate() {
        return uploadRate;
    }

    public int getLeftUntilDone() {
        return leftUntilDone;
    }

    public long getUploadedSize() {
        return uploadedSize;
    }

    public double getUploadRatio() {
        return uploadRatio;
    }

    public Error getError() {
        if (error == null) error = Error.getById(errorId);
        return error;
    }

    public String getErrorMessage() {
        return errorString;
    }

    @Override
    public String toString() {
        return "Torrent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addedData=" + addedData +
                ", totalSize=" + totalSize +
                ", percentDone=" + percentDone +
                ", status=" + Status.fromValue(status) + "(" + status + ")" +
                ", downloadRate=" + downloadRate +
                ", uploadRate=" + uploadRate +
                ", leftUntilDone=" + leftUntilDone +
                ", uploadedSize=" + uploadedSize +
                ", uploadRatio=" + uploadRatio +
                '}';
    }

    public static enum Status {
        UNKNOWN(-1),
        STOPPED(0),
        CHECK_WAIT(1),
        CHECK(2),
        DOWNLOAD_WAIT(3),
        DOWNLOAD(4),
        SEED_WAIT(5),
        SEED(6);

        private int value;

        private Status(int value) {
            this.value = value;
        }

        public static Status fromValue(int value) {
            for (Status status : values()) {
                if (status.value == value)
                    return status;
            }
            return UNKNOWN;
        }
    }

    public static enum Error {
        UNKNOWN(-1, false),
        NONE(0, false),
        TRACKER_WARNING(1, true),
        TRACKER_ERROR(2, false),
        LOCAL_ERROR(3, false);

        private int id;
        private boolean isWarning;

        private Error(int id, boolean isWarning) {
            this.id = id;
            this.isWarning = isWarning;
        }

        public boolean isWarning() {
            return isWarning;
        }

        public static Error getById(int id) {
            for (Error e : Error.values()) {
                if (e.id == id) return e;
            }
            return UNKNOWN;
        }
    }
}
