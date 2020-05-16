package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

import net.yupol.transmissionremote.app.model.ID;

public class Torrent implements ID, Parcelable {
    @Key private int id;
    @Key private String name;
    @Key private long addedDate;
    @Key private long totalSize;
    @Key private double percentDone;
    @Key private int status;
    @Key("rateDownload") private int downloadRate;
    @Key("rateUpload") private int uploadRate;
    @Key private long eta;
    @Key("uploadedEver") private long uploadedSize;
    @Key private double uploadRatio;
    @Key("error") private int errorId;
    private Error error;
    @Key private String errorString;
    @Key private boolean isFinished;
    @Key private long sizeWhenDone;
    @Key private long leftUntilDone;
    @Key private int peersGettingFromUs;
    @Key private int peersSendingToUs;
    @Key private int webseedsSendingToUs;
    @Key private int queuePosition;
    @Key private double recheckProgress;
    @Key private long doneDate;

    public Torrent() {}

    private Torrent(Parcel in) {
        id = in.readInt();
        name = in.readString();
        addedDate = in.readLong();
        totalSize = in.readLong();
        percentDone = in.readDouble();
        recheckProgress = in.readDouble();
        status = in.readInt();
        downloadRate = in.readInt();
        uploadRate = in.readInt();
        eta = in.readLong();
        uploadedSize = in.readLong();
        uploadRatio = in.readDouble();
        errorId = in.readInt();
        errorString = in.readString();
        isFinished = in.readInt() != 0;
        sizeWhenDone = in.readLong();
        leftUntilDone = in.readLong();
        peersGettingFromUs = in.readInt();
        peersSendingToUs = in.readInt();
        webseedsSendingToUs = in.readInt();
        queuePosition = in.readInt();
        doneDate = in.readLong();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getAddedDate() {
        return addedDate;
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

    public long getEta() {
        return eta;
    }

    public long getUploadedSize() {
        return uploadedSize;
    }

    public double getUploadRatio() {
        return uploadRatio;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public int getErrorId() {
        return errorId;
    }

    public Error getError() {
        if (error == null) error = Error.getById(errorId);
        return error;
    }

    public String getErrorMessage() {
        return errorString;
    }

    public boolean isActive() {
        return peersGettingFromUs > 0
                || peersSendingToUs > 0
                || webseedsSendingToUs > 0
                || isChecking()
                || isDownloading();
    }

    public boolean isChecking() {
        return status == Status.CHECK.value;
    }

    public boolean isSeeding() {
        return status == Status.SEED.value || status == Status.SEED_WAIT.value;
    }

    public boolean isDownloading() {
        return status == Status.DOWNLOAD.value || status == Status.DOWNLOAD_WAIT.value;
    }

    public boolean isPaused() {
        return status == Status.STOPPED.value;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isCompleted() {
        return leftUntilDone <= 0 && sizeWhenDone > 0;
    }

    public long getSizeWhenDone() {
        return sizeWhenDone;
    }

    public long getLeftUntilDone() {
        return leftUntilDone;
    }

    public double getRecheckProgress() {
        return recheckProgress;
    }

    public long getDoneDate() {
        return doneDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeLong(addedDate);
        out.writeLong(totalSize);
        out.writeDouble(percentDone);
        out.writeDouble(recheckProgress);
        out.writeInt(status);
        out.writeInt(downloadRate);
        out.writeInt(uploadRate);
        out.writeLong(eta);
        out.writeLong(uploadedSize);
        out.writeDouble(uploadRatio);
        out.writeInt(errorId);
        out.writeString(errorString);
        out.writeInt(isFinished ? 1 : 0);
        out.writeLong(sizeWhenDone);
        out.writeLong(leftUntilDone);
        out.writeInt(peersGettingFromUs);
        out.writeInt(peersSendingToUs);
        out.writeInt(webseedsSendingToUs);
        out.writeInt(queuePosition);
        out.writeLong(doneDate);
    }

    public static final Creator<Torrent> CREATOR = new Creator<Torrent>() {
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
                ", addedDate=" + addedDate +
                ", totalSize=" + totalSize +
                ", percentDone=" + percentDone +
                ", status=" + status +
                ", downloadRate=" + downloadRate +
                ", uploadRate=" + uploadRate +
                ", uploadedSize=" + uploadedSize +
                ", uploadRatio=" + uploadRatio +
                ", errorId=" + errorId +
                ", error=" + error +
                ", errorString='" + errorString + '\'' +
                ", isFinished=" + isFinished +
                ", doneDate=" + doneDate +
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

    public static class Builder {

        private Torrent torrent;

        public Builder() {
            torrent = new Torrent();
        }

        public Builder doneDate(long date) {
            torrent.doneDate = date;
            return this;
        }

        public Torrent build() {
            return torrent;
        }
    }
}
