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
}

/*
ID = "id"
ADDED_DATE = "addedDate"
NAME = "name"
TOTAL_SIZE = "totalSize"
ERROR = "error"
ERROR_STRING = "errorString"
ETA = "eta"
IS_FINISHED = "isFinished"
IS_STALLED = "isStalled"
LEFT_UNTIL_DONE = "leftUntilDone"
METADATA_PERCENT_COMPLETE = "metadataPercentComplete"
PEERS_CONNECTED = "peersConnected"
PEERS_GETTING_FROM_US = "peersGettingFromUs"
PEERS_SENDING_TO_US = "peersSendingToUs"
PERCENT_DONE = "percentDone"
QUEUE_POSITION = "queuePosition"
RATE_DOWNLOAD = "rateDownload"
RATE_UPLOAD = "rateUpload"
RECHECK_PROGRESS = "recheckProgress"
SEED_RATION_MODE = "seedRatioMode"
SEED_RATION_LIMIT = "seedRatioLimit"
SIZE_WHEN_DONE = "sizeWhenDone"
STATUS = "status"
TRACKERS = "trackers"
DOWNLOAD_DIR = "downloadDir"
UPLOADED_EVER = "uploadedEver"
UPLOAD_RATION = "uploadRatio"
WEBSEEDS_SENDING_TO_US = "webseedsSendingToUs"
ACTIVITY_DATE = "activityDate"
CORRUPT_EVER = "corruptEver"
DESIRED_AVAILABLE = "desiredAvailable"
DOWNLOAD_EVER = "downloadedEver"
FILE_STATS = "fileStats"
HAVE_UNCHECKED = "haveUnchecked"
HAVE_VALID = "haveValid"
PEERS = "peers"
START_DATE = "startDate"
TRACKER_STATS = "trackerStats"
COMMENT = "comment"
CREATOR = "creator"
DATE_CREATED = "dateCreated"
FILES = "files"
HASH_STRING = "hashString"
IS_PRIVATE = "isPrivate"
PIECE_COUNT = "pieceCount"
PIECE_SIZE = "pieceSize"
*/
