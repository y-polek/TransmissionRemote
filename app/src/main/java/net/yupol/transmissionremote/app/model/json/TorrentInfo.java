package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.api.client.util.Key;

import net.yupol.transmissionremote.app.model.TorrentMetadata;
import net.yupol.transmissionremote.app.model.limitmode.IdleLimitMode;
import net.yupol.transmissionremote.app.model.limitmode.LimitMode;
import net.yupol.transmissionremote.app.model.limitmode.RatioLimitMode;
import net.yupol.transmissionremote.app.utils.ParcelableUtils;

public class TorrentInfo implements Parcelable {

    @Key("torrents") private Item[] items;

    public TorrentInfo() {}

    private TorrentInfo(Parcel in) {
        Item i = new Item();
        items = new Item[] { i };

        i.id = in.readInt();
        i.files = in.createTypedArray(File.CREATOR);
        i.fileStats = in.createTypedArray(FileStat.CREATOR);
        i.transferPriorityValue = in.readInt();
        i.honorsSessionLimits = in.readInt() != 0;
        i.downloadLimited = in.readInt() != 0;
        i.downloadLimit = in.readLong();
        i.uploadLimited = in.readInt() != 0;
        i.uploadLimit = in.readLong();
        i.seedRatioLimit = in.readDouble();
        i.seedRatioModeValue = in.readInt();
        i.seedIdleLimit = in.readInt();
        i.seedIdleModeValue = in.readInt();
        i.haveUnchecked = in.readLong();
        i.haveValid = in.readLong();
        i.sizeWhenDone = in.readLong();
        i.leftUntilDone = in.readLong();
        i.desiredAvailable = in.readLong();
        i.pieceCount = in.readLong();
        i.pieceSize = in.readLong();
        i.downloadDir = in.readString();
        i.isPrivate = in.readInt() != 0;
        i.creator = in.readString();
        i.dateCreated = in.readLong();
        i.comment = in.readString();
        i.downloadedEver = in.readLong();
        i.corruptEver = in.readLong();
        i.uploadedEver = in.readLong();
        i.addedDate = in.readLong();
        i.activityDate = in.readLong();
        i.secondsDownloading = in.readLong();
        i.secondsSeeding = in.readLong();
        i.peers = ParcelableUtils.toArrayOfType(Peer.class,
                in.readParcelableArray(Peer.class.getClassLoader()));
        i.trackers = ParcelableUtils.toArrayOfType(Tracker.class,
                in.readParcelableArray(Tracker.class.getClassLoader()));
        i.trackerStats = ParcelableUtils.toArrayOfType(TrackerStats.class,
                in.readParcelableArray(TrackerStats.class.getClassLoader()));
        i.magnetLink = in.readString();
    }

    public TransferPriority getTransferPriority() {
        Item i = items[0];
        if (i.transferPriority == null) i.transferPriority = TransferPriority.fromModelValue(i.transferPriorityValue);
        return i.transferPriority;
    }

    public int getId() {
        return items[0].id;
    }

    public File[] getFiles() {
        return items.length == 1 ? items[0].files : null;
    }

    public FileStat[] getFileStats() {
        return items[0].fileStats;
    }

    public boolean isSessionLimitsHonored() {
        return items[0].honorsSessionLimits;
    }

    public boolean isDownloadLimited() {
        return items[0].downloadLimited;
    }

    public long getDownloadLimit() {
        return items[0].downloadLimit;
    }

    public boolean isUploadLimited() {
        return items[0].uploadLimited;
    }

    public long getUploadLimit() {
        return items[0].uploadLimit;
    }

    public double getSeedRatioLimit() {
        return items[0].seedRatioLimit;
    }

    public LimitMode getSeedRatioMode() {
        Item i = items[0];
        if (i.seedRatioMode == null) i.seedRatioMode = RatioLimitMode.fromValue(i.seedRatioModeValue);
        return i.seedRatioMode;
    }

    public int getSeedIdleLimit() {
        return items[0].seedIdleLimit;
    }

    public LimitMode getSeedIdleMode() {
        Item i = items[0];
        if (i.seedIdleMode == null) i.seedIdleMode = IdleLimitMode.fromValue(i.seedIdleModeValue);
        return i.seedIdleMode;
    }

    public long getHaveUnchecked() {
        return items[0].haveUnchecked;
    }

    public long getHaveValid() {
        return items[0].haveValid;
    }

    public long getSizeWhenDone() {
        return items[0].sizeWhenDone;
    }

    public long getLeftUntilDone() {
        return items[0].leftUntilDone;
    }

    public long getDesiredAvailable() {
        return items[0].desiredAvailable;
    }

    public double getHavePercent() {
        long sizeWhenDone = getSizeWhenDone();
        long leftUntilDone = getLeftUntilDone();
        return 100.0 * (sizeWhenDone - leftUntilDone)/(double) sizeWhenDone;
    }

    public double getAvailablePercent() {
        long sizeWhenDone = getSizeWhenDone();
        if (sizeWhenDone <= 0) return 0.0;
        return (100.0 * (getHaveValid() + getHaveUnchecked() + getDesiredAvailable())) / sizeWhenDone;
    }

    public long getAddedDate() {
        return items[0].addedDate;
    }

    public long getActivityDate() {
        return items[0].activityDate;
    }

    public long getPieceCount() {
        return items[0].pieceCount;
    }

    public long getPieceSize() {
        return items[0].pieceSize;
    }

    public String getDownloadDir() {
        return items[0].downloadDir;
    }

    public boolean isPrivate() {
        return items[0].isPrivate;
    }

    public String getCreator() {
        return items[0].creator;
    }

    public long getDateCreated() {
        return items[0].dateCreated;
    }

    public String getComment() {
        return items[0].comment;
    }

    public long getDownloadedEver() {
        return items[0].downloadedEver;
    }

    public long getCorruptEver() {
        return items[0].corruptEver;
    }

    public long getUploadedEver() {
        return items[0].uploadedEver;
    }

    public long getSecondsDownloading() {
        return items[0].secondsDownloading;
    }

    public long getSecondsSeeding() {
        return items[0].secondsSeeding;
    }

    public Peer[] getPeers() {
        return items[0].peers;
    }

    public Tracker[] getTrackers() {
        return items[0].trackers;
    }

    @NonNull
    public TrackerStats[] getTrackerStats() {
        return items[0].trackerStats != null ? items[0].trackerStats : new TrackerStats[0];
    }

    public String getMagnetLink() {
        return items[0].magnetLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Item i = items[0];
        out.writeInt(i.id);
        out.writeTypedArray(i.files, flags);
        out.writeTypedArray(i.fileStats, flags);
        out.writeInt(i.transferPriorityValue);
        out.writeInt(i.honorsSessionLimits ? 1 : 0);
        out.writeInt(i.downloadLimited ? 1 : 0);
        out.writeLong(i.downloadLimit);
        out.writeInt(i.uploadLimited ? 1 : 0);
        out.writeLong(i.uploadLimit);
        out.writeDouble(i.seedRatioLimit);
        out.writeInt(i.seedRatioModeValue);
        out.writeInt(i.seedIdleLimit);
        out.writeInt(i.seedIdleModeValue);
        out.writeLong(i.haveUnchecked);
        out.writeLong(i.haveValid);
        out.writeLong(i.sizeWhenDone);
        out.writeLong(i.leftUntilDone);
        out.writeLong(i.desiredAvailable);
        out.writeLong(i.pieceCount);
        out.writeLong(i.pieceSize);
        out.writeString(i.downloadDir);
        out.writeInt(i.isPrivate ? 1 : 0);
        out.writeString(i.creator);
        out.writeLong(i.dateCreated);
        out.writeString(i.comment);
        out.writeLong(i.downloadedEver);
        out.writeLong(i.corruptEver);
        out.writeLong(i.uploadedEver);
        out.writeLong(i.addedDate);
        out.writeLong(i.activityDate);
        out.writeLong(i.secondsDownloading);
        out.writeLong(i.secondsSeeding);
        out.writeParcelableArray(i.peers, flags);
        out.writeParcelableArray(i.trackers, flags);
        out.writeParcelableArray(i.trackerStats, flags);
        out.writeString(i.magnetLink);
    }

    public static final Creator<TorrentInfo> CREATOR = new Creator<TorrentInfo>() {
        @Override
        public TorrentInfo createFromParcel(Parcel in) {
            return new TorrentInfo(in);
        }

        @Override
        public TorrentInfo[] newArray(int size) {
            return new TorrentInfo[size];
        }
    };

    public static class Item {
        @Key private int id;
        @Key private File[] files;
        @Key private FileStat[] fileStats;
        @Key("bandwidthPriority")
        private int transferPriorityValue;
        private TransferPriority transferPriority;
        @Key private boolean honorsSessionLimits;
        @Key private boolean downloadLimited;
        @Key private long downloadLimit;
        @Key private boolean uploadLimited;
        @Key private long uploadLimit;
        @Key private double seedRatioLimit;
        @Key("seedRatioMode") private int seedRatioModeValue;
        private LimitMode seedRatioMode;
        @Key private int seedIdleLimit;
        @Key("seedIdleMode") private int seedIdleModeValue;
        private LimitMode seedIdleMode;
        @Key private long haveUnchecked;
        @Key private long haveValid;
        @Key private long sizeWhenDone;
        @Key private long leftUntilDone;
        @Key private long desiredAvailable;
        @Key private long pieceCount;
        @Key private long pieceSize;
        @Key private String downloadDir;
        @Key private boolean isPrivate;
        @Key private String creator;
        @Key private long dateCreated;
        @Key private String comment;
        @Key private long downloadedEver;
        @Key private long corruptEver;
        @Key private long uploadedEver;
        @Key private long addedDate;
        @Key private long activityDate;
        @Key private long secondsDownloading;
        @Key private long secondsSeeding;
        @Key private Peer[] peers;
        @Key private Tracker[] trackers;
        @Key private TrackerStats[] trackerStats;
        @Key(TorrentMetadata.MAGNET_LINK) private String magnetLink;
    }
}
