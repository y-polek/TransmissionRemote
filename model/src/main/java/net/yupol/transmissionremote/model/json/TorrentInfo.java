package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import net.yupol.transmissionremote.data.api.model.TorrentInfoEntity;
import net.yupol.transmissionremote.model.limitmode.IdleLimitMode;
import net.yupol.transmissionremote.model.limitmode.LimitMode;
import net.yupol.transmissionremote.model.limitmode.RatioLimitMode;
import net.yupol.transmissionremote.model.mapper.FileMapper;
import net.yupol.transmissionremote.model.mapper.PeerMapper;
import net.yupol.transmissionremote.model.mapper.TrackerMapper;
import net.yupol.transmissionremote.model.utils.Parcelables;

public class TorrentInfo implements Parcelable {

    @Json(name = "id") private int id;
    @Json(name = "files") private File[] files;
    @Json(name = "fileStats") private FileStat[] fileStats;
    @Json(name = "bandwidthPriority") private int transferPriorityValue;
    private transient TransferPriority transferPriority;
    @Json(name = "honorsSessionLimits") private boolean honorsSessionLimits;
    @Json(name = "downloadLimited") private boolean downloadLimited;
    @Json(name = "downloadLimit") private long downloadLimit;
    @Json(name = "uploadLimited") private boolean uploadLimited;
    @Json(name = "uploadLimit") private long uploadLimit;
    @Json(name = "seedRatioLimit") private double seedRatioLimit;
    @Json(name = "seedRatioMode") private int seedRatioModeValue;
    private transient LimitMode seedRatioMode;
    @Json(name = "seedIdleLimit") private int seedIdleLimit;
    @Json(name = "seedIdleMode") private int seedIdleModeValue;
    private transient LimitMode seedIdleMode;
    @Json(name = "haveUnchecked") private long haveUnchecked;
    @Json(name = "haveValid") private long haveValid;
    @Json(name = "sizeWhenDone") private long sizeWhenDone;
    @Json(name = "leftUntilDone") private long leftUntilDone;
    @Json(name = "desiredAvailable") private long desiredAvailable;
    @Json(name = "pieceCount") private long pieceCount;
    @Json(name = "pieceSize") private long pieceSize;
    @Json(name = "downloadDir") private String downloadDir;
    @Json(name = "isPrivate") private boolean isPrivate;
    @Json(name = "creator") private String creator;
    @Json(name = "dateCreated") private long dateCreated;
    @Json(name = "comment") private String comment;
    @Json(name = "downloadedEver") private long downloadedEver;
    @Json(name = "corruptEver") private long corruptEver;
    @Json(name = "uploadedEver") private long uploadedEver;
    @Json(name = "addedDate") private long addedDate;
    @Json(name = "activityDate") private long activityDate;
    @Json(name = "secondsDownloading") private long secondsDownloading;
    @Json(name = "secondsSeeding") private long secondsSeeding;
    @Json(name = "peers") private Peer[] peers;
    @Json(name = "trackers") private Tracker[] trackers;
    @Json(name = "trackerStats") private TrackerStats[] trackerStats;

    public TorrentInfo() {}

    private TorrentInfo(Parcel in) {
        id = in.readInt();
        files = in.createTypedArray(File.CREATOR);
        fileStats = in.createTypedArray(FileStat.CREATOR);
        transferPriorityValue = in.readInt();
        honorsSessionLimits = in.readInt() != 0;
        downloadLimited = in.readInt() != 0;
        downloadLimit = in.readLong();
        uploadLimited = in.readInt() != 0;
        uploadLimit = in.readLong();
        seedRatioLimit = in.readDouble();
        seedRatioModeValue = in.readInt();
        seedIdleLimit = in.readInt();
        seedIdleModeValue = in.readInt();
        haveUnchecked = in.readLong();
        haveValid = in.readLong();
        sizeWhenDone = in.readLong();
        leftUntilDone = in.readLong();
        desiredAvailable = in.readLong();
        pieceCount = in.readLong();
        pieceSize = in.readLong();
        downloadDir = in.readString();
        isPrivate = in.readInt() != 0;
        creator = in.readString();
        dateCreated = in.readLong();
        comment = in.readString();
        downloadedEver = in.readLong();
        corruptEver = in.readLong();
        uploadedEver = in.readLong();
        addedDate = in.readLong();
        activityDate = in.readLong();
        secondsDownloading = in.readLong();
        secondsSeeding = in.readLong();
        peers = Parcelables.toArrayOfType(Peer.class,
                in.readParcelableArray(Peer.class.getClassLoader()));
        trackers = Parcelables.toArrayOfType(Tracker.class,
                in.readParcelableArray(Tracker.class.getClassLoader()));
        trackerStats = Parcelables.toArrayOfType(TrackerStats.class,
                in.readParcelableArray(TrackerStats.class.getClassLoader()));
    }

    public TransferPriority getTransferPriority() {
        if (transferPriority == null) transferPriority = TransferPriority.fromModelValue(transferPriorityValue);
        return transferPriority;
    }

    public int getId() {
        return id;
    }

    public File[] getFiles() {
        return files;
    }

    public FileStat[] getFileStats() {
        return fileStats;
    }

    public boolean isSessionLimitsHonored() {
        return honorsSessionLimits;
    }

    public boolean isDownloadLimited() {
        return downloadLimited;
    }

    public long getDownloadLimit() {
        return downloadLimit;
    }

    public boolean isUploadLimited() {
        return uploadLimited;
    }

    public long getUploadLimit() {
        return uploadLimit;
    }

    public double getSeedRatioLimit() {
        return seedRatioLimit;
    }

    public LimitMode getSeedRatioMode() {
        if (seedRatioMode == null) seedRatioMode = RatioLimitMode.fromValue(seedRatioModeValue);
        return seedRatioMode;
    }

    public int getSeedIdleLimit() {
        return seedIdleLimit;
    }

    public LimitMode getSeedIdleMode() {
        if (seedIdleMode == null) seedIdleMode = IdleLimitMode.fromValue(seedIdleModeValue);
        return seedIdleMode;
    }

    public long getHaveUnchecked() {
        return haveUnchecked;
    }

    public long getHaveValid() {
        return haveValid;
    }

    public long getSizeWhenDone() {
        return sizeWhenDone;
    }

    public long getLeftUntilDone() {
        return leftUntilDone;
    }

    public long getDesiredAvailable() {
        return desiredAvailable;
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
        return addedDate;
    }

    public long getActivityDate() {
        return activityDate;
    }

    public long getPieceCount() {
        return pieceCount;
    }

    public long getPieceSize() {
        return pieceSize;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getCreator() {
        return creator;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public String getComment() {
        return comment;
    }

    public long getDownloadedEver() {
        return downloadedEver;
    }

    public long getCorruptEver() {
        return corruptEver;
    }

    public long getUploadedEver() {
        return uploadedEver;
    }

    public long getSecondsDownloading() {
        return secondsDownloading;
    }

    public long getSecondsSeeding() {
        return secondsSeeding;
    }

    public Peer[] getPeers() {
        return peers;
    }

    public Tracker[] getTrackers() {
        return trackers;
    }

    public TrackerStats[] getTrackerStats() {
        return trackerStats;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeTypedArray(files, flags);
        out.writeTypedArray(fileStats, flags);
        out.writeInt(transferPriorityValue);
        out.writeInt(honorsSessionLimits ? 1 : 0);
        out.writeInt(downloadLimited ? 1 : 0);
        out.writeLong(downloadLimit);
        out.writeInt(uploadLimited ? 1 : 0);
        out.writeLong(uploadLimit);
        out.writeDouble(seedRatioLimit);
        out.writeInt(seedRatioModeValue);
        out.writeInt(seedIdleLimit);
        out.writeInt(seedIdleModeValue);
        out.writeLong(haveUnchecked);
        out.writeLong(haveValid);
        out.writeLong(sizeWhenDone);
        out.writeLong(leftUntilDone);
        out.writeLong(desiredAvailable);
        out.writeLong(pieceCount);
        out.writeLong(pieceSize);
        out.writeString(downloadDir);
        out.writeInt(isPrivate ? 1 : 0);
        out.writeString(creator);
        out.writeLong(dateCreated);
        out.writeString(comment);
        out.writeLong(downloadedEver);
        out.writeLong(corruptEver);
        out.writeLong(uploadedEver);
        out.writeLong(addedDate);
        out.writeLong(activityDate);
        out.writeLong(secondsDownloading);
        out.writeLong(secondsSeeding);
        out.writeParcelableArray(peers, flags);
        out.writeParcelableArray(trackers, flags);
        out.writeParcelableArray(trackerStats, flags);
    }

    public static TorrentInfo fromEntity(TorrentInfoEntity entity) {
        TorrentInfo info = new TorrentInfo();
        info.id = entity.id;
        info.files = FileMapper.toViewModel(entity.files);
        info.fileStats = FileMapper.toViewModel(entity.fileStats);
        info.transferPriorityValue = entity.transferPriorityValue;
        info.honorsSessionLimits = entity.honorsSessionLimits;
        info.downloadLimited = entity.downloadLimited;
        info.downloadLimit = entity.downloadLimit;
        info.uploadLimited = entity.uploadLimited;
        info.uploadLimit = entity.uploadLimit;
        info.seedRatioLimit = entity.seedRatioLimit;
        info.seedRatioModeValue = entity.seedRatioModeValue;
        info.seedIdleLimit = entity.seedIdleLimit;
        info.seedIdleModeValue = entity.seedIdleModeValue;
        info.haveUnchecked = entity.haveUnchecked;
        info.haveValid = entity.haveValid;
        info.sizeWhenDone = entity.sizeWhenDone;
        info.leftUntilDone = entity.leftUntilDone;
        info.desiredAvailable = entity.desiredAvailable;
        info.pieceCount = entity.pieceCount;
        info.pieceSize = entity.pieceSize;
        info.downloadDir = entity.downloadDir;
        info.isPrivate = entity.isPrivate;
        info.creator = entity.creator;
        info.dateCreated = entity.dateCreated;
        info.comment = entity.comment;
        info.downloadedEver = entity.downloadedEver;
        info.corruptEver = entity.corruptEver;
        info.uploadedEver = entity.uploadedEver;
        info.addedDate = entity.addedDate;
        info.activityDate = entity.activityDate;
        info.secondsDownloading = entity.secondsDownloading;
        info.secondsSeeding = entity.secondsSeeding;
        info.peers = PeerMapper.toViewModel(entity.peers);
        info.trackers = TrackerMapper.toViewModel(entity.trackers);
        info.trackerStats = TrackerMapper.toViewModel(entity.trackerStats);
        return info;
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
}
