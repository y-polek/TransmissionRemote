package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class Torrent implements Parcelable {
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

    public Torrent() {}

    private Torrent(Parcel in) {
        id = in.readInt();
        name = in.readString();
        addedData = in.readLong();
        totalSize = in.readLong();
        percentDone = in.readDouble();
        status = in.readInt();
        downloadRate = in.readInt();
        uploadRate = in.readInt();
        leftUntilDone = in.readInt();
        uploadedSize = in.readLong();
        uploadRatio = in.readDouble();
        errorId = in.readInt();
        errorString = in.readString();
    }

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeLong(addedData);
        out.writeLong(totalSize);
        out.writeDouble(percentDone);
        out.writeInt(status);
        out.writeInt(downloadRate);
        out.writeInt(uploadRate);
        out.writeInt(leftUntilDone);
        out.writeLong(uploadedSize);
        out.writeDouble(uploadRatio);
        out.writeInt(errorId);
        out.writeString(errorString);
    }

    public static final Parcelable.Creator<Torrent> CREATOR = new Creator<Torrent>() {
        @Override
        public Torrent createFromParcel(Parcel in) {
            return new Torrent(in);
        }

        @Override
        public Torrent[] newArray(int size) {
            return new Torrent[size];
        }
    };

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

    public enum Status {
        UNKNOWN(-1),
        STOPPED(0),
        CHECK_WAIT(1),
        CHECK(2),
        DOWNLOAD_WAIT(3),
        DOWNLOAD(4),
        SEED_WAIT(5),
        SEED(6);

        private int value;

        Status(int value) {
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

    public enum Error {
        UNKNOWN(-1, false),
        NONE(0, false),
        TRACKER_WARNING(1, true),
        TRACKER_ERROR(2, false),
        LOCAL_ERROR(3, false);

        private int id;
        private boolean isWarning;

        Error(int id, boolean isWarning) {
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
