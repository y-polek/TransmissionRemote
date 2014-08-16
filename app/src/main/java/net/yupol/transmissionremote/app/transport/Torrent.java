package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Torrent {

    private static final String TAG = Torrent.class.getSimpleName();

    private int id;
    private String name;
    private long addedData;
    private long totalSize;
    private double percentDone;
    private Status status;
    private int downloadRate;
    private int uploadRate;

    public Torrent(JSONObject obj) {
        id = obj.optInt(Metadata.ID);
        name = obj.optString(Metadata.NAME);
        addedData = obj.optLong(Metadata.ADDED_DATE);
        totalSize = obj.optLong(Metadata.TOTAL_SIZE);
        percentDone = obj.optDouble(Metadata.PERCENT_DONE);
        status = Status.fromValue(obj.optInt(Metadata.STATUS, -1));
        downloadRate = obj.optInt(Metadata.RATE_DOWNLOAD);
        uploadRate = obj.optInt(Metadata.RATE_UPLOAD);
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
        return status;
    }

    public int getDownloadRate() {
        return downloadRate;
    }

    public int getUploadRate() {
        return uploadRate;
    }

    @Override
    public String toString() {
        return "Torrent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addedData=" + addedData +
                ", totalSize=" + totalSize +
                ", percentDone=" + percentDone +
                ", status=" + status +
                '}';
    }

    public static List<Torrent> fromArray(JSONArray array) {
        List<Torrent> torrents = new ArrayList<>(array.length());
        for (int i=0; i<array.length(); i++) {
            JSONObject torrentObj = array.optJSONObject(i);
            if (torrentObj != null) {
                torrents.add(new Torrent(torrentObj));
            } else {
                Log.e(TAG, "Non object in torrents array at index " + i);
            }
        }
        return torrents;
    }

    public static class Metadata {
        public static final String ID = "id";
        public static final String ADDED_DATE = "addedDate";
        public static final String NAME = "name";
        public static final String TOTAL_SIZE = "totalSize";
        public static final String ERROR = "error";
        public static final String ERROR_STRING = "errorString";
        public static final String ETA = "eta";
        public static final String IS_FINISHED = "isFinished";
        public static final String IS_STALLED = "isStalled";
        public static final String LEFT_UNTIL_DONE = "leftUntilDone";
        public static final String METADATA_PERCENT_COMPLETE = "metadataPercentComplete";
        public static final String PEERS_CONNECTED = "peersConnected";
        public static final String PEERS_GETTING_FROM_US = "peersGettingFromUs";
        public static final String PEERS_SENDING_TO_US = "peersSendingToUs";
        public static final String PERCENT_DONE = "percentDone";
        public static final String QUEUE_POSITION = "queuePosition";
        public static final String RATE_DOWNLOAD = "rateDownload";
        public static final String RATE_UPLOAD = "rateUpload";
        public static final String RECHECK_PROGRESS = "recheckProgress";
        public static final String SEED_RATION_MODE = "seedRatioMode";
        public static final String SEED_RATION_LIMIT = "seedRatioLimit";
        public static final String SIZE_WHEN_DONE = "sizeWhenDone";
        public static final String STATUS = "status";
        public static final String TRACKERS = "trackers";
        public static final String DOWNLOAD_DIR = "downloadDir";
        public static final String UPLOADED_EVER = "uploadedEver";
        public static final String UPLOAD_RATION = "uploadRatio";
        public static final String WEBSEEDS_SENDING_TO_US = "webseedsSendingToUs";
        public static final String ACTIVITY_DATE = "activityDate";
        public static final String CORRUPT_EVER = "corruptEver";
        public static final String DESIRED_AVAILABLE = "desiredAvailable";
        public static final String DOWNLOAD_EVER = "downloadedEver";
        public static final String FILE_STATS = "fileStats";
        public static final String HAVE_UNCHECKED = "haveUnchecked";
        public static final String HAVE_VALID = "haveValid";
        public static final String PEERS = "peers";
        public static final String START_DATE = "startDate";
        public static final String TRACKER_STATS = "trackerStats";
        public static final String COMMENT = "comment";
        public static final String CREATOR = "creator";
        public static final String DATE_CREATED = "dateCreated";
        public static final String FILES = "files";
        public static final String HASH_STRING = "hashString";
        public static final String IS_PRIVATE = "isPrivate";
        public static final String PIECE_COUNT = "pieceCount";
        public static final String PIECE_SIZE = "pieceSize";
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
