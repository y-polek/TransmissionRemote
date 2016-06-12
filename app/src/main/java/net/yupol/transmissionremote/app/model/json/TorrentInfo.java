package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

import net.yupol.transmissionremote.app.model.limitmode.IdleLimitMode;
import net.yupol.transmissionremote.app.model.limitmode.LimitMode;
import net.yupol.transmissionremote.app.model.limitmode.RatioLimitMode;

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
        i.availableSize = in.readLong();
        i.pieceCount = in.readLong();
        i.pieceSize = in.readLong();
        i.downloadDir = in.readString();
        i.hashString = in.readString();
        i.isPrivate = in.readInt() != 0;
        i.creator = in.readString();
        i.dateCreated = in.readLong();
        i.comment = in.readString();
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

    public long getAvailableSize() {
        return items[0].availableSize;
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

    public String getHashString() {
        return items[0].hashString;
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
        out.writeLong(i.availableSize);
        out.writeLong(i.pieceCount);
        out.writeLong(i.pieceSize);
        out.writeString(i.downloadDir);
        out.writeString(i.hashString);
        out.writeInt(i.isPrivate ? 1 : 0);
        out.writeString(i.creator);
        out.writeLong(i.dateCreated);
        out.writeString(i.comment);
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
        @Key("desiredAvailable") private long availableSize;
        @Key private long pieceCount;
        @Key private long pieceSize;
        @Key private String downloadDir;
        @Key private String hashString;
        @Key private boolean isPrivate;
        @Key private String creator;
        @Key private long dateCreated;
        @Key private String comment;
    }
}
