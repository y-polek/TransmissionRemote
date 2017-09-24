package net.yupol.transmissionremote.app.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class TrackerStats implements Parcelable {

    @Key public int id;
    @Key public String announce;
    @Key public int announceState;
    @Key public int downloadCount;
    @Key public boolean hasAnnounced;
    @Key public boolean hasScraped;
    @Key public String host;
    @Key public boolean isBackup;
    @Key public int lastAnnouncePeerCount;
    @Key public String lastAnnounceResult;
    @Key public long lastAnnounceStartTime;
    @Key public boolean lastAnnounceSucceeded;
    @Key public long lastAnnounceTime;
    @Key public boolean lastAnnounceTimedOut;
    @Key public String lastScrapeResult;
    @Key public long lastScrapeStartTime;
    @Key public boolean lastScrapeSucceeded;
    @Key public long lastScrapeTime;
    @Key public byte lastScrapeTimedOut;
    @Key public int leecherCount;
    @Key public long nextAnnounceTime;
    @Key public long nextScrapeTime;
    @Key public String scrape;
    @Key public int scrapeState;
    @Key public int seederCount;
    @Key public int tier;

    public static final Creator<TrackerStats> CREATOR = new Creator<TrackerStats>() {
        @Override
        public TrackerStats createFromParcel(Parcel in) {
            TrackerStats stats = new TrackerStats();
            stats.id = in.readInt();
            stats.announce = in.readString();
            stats.announceState = in.readInt();
            stats.downloadCount = in.readInt();
            stats.hasAnnounced = in.readByte() != 0;
            stats.hasScraped = in.readByte() != 0;
            stats.host = in.readString();
            stats.isBackup = in.readByte() != 0;
            stats.lastAnnouncePeerCount = in.readInt();
            stats.lastAnnounceResult = in.readString();
            stats.lastAnnounceStartTime = in.readLong();
            stats.lastAnnounceSucceeded = in.readByte() != 0;
            stats.lastAnnounceTime = in.readLong();
            stats.lastAnnounceTimedOut = in.readByte() != 0;
            stats.lastScrapeResult = in.readString();
            stats.lastScrapeStartTime = in.readLong();
            stats.lastScrapeSucceeded = in.readByte() != 0;
            stats.lastScrapeTime = in.readLong();
            stats.lastScrapeTimedOut = in.readByte();
            stats.leecherCount = in.readInt();
            stats.nextAnnounceTime = in.readLong();
            stats.nextScrapeTime = in.readLong();
            stats.scrape = in.readString();
            stats.scrapeState = in.readInt();
            stats.seederCount = in.readInt();
            stats.tier = in.readInt();
            return stats;
        }

        @Override
        public TrackerStats[] newArray(int size) {
            return new TrackerStats[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(announce);
        parcel.writeInt(announceState);
        parcel.writeInt(downloadCount);
        parcel.writeByte((byte) (hasAnnounced ? 1 : 0));
        parcel.writeByte((byte) (hasScraped ? 1 : 0));
        parcel.writeString(host);
        parcel.writeByte((byte) (isBackup ? 1 : 0));
        parcel.writeInt(lastAnnouncePeerCount);
        parcel.writeString(lastAnnounceResult);
        parcel.writeLong(lastAnnounceStartTime);
        parcel.writeByte((byte) (lastAnnounceSucceeded ? 1 : 0));
        parcel.writeLong(lastAnnounceTime);
        parcel.writeByte((byte) (lastAnnounceTimedOut ? 1 : 0));
        parcel.writeString(lastScrapeResult);
        parcel.writeLong(lastScrapeStartTime);
        parcel.writeByte((byte) (lastScrapeSucceeded ? 1 : 0));
        parcel.writeLong(lastScrapeTime);
        parcel.writeByte(lastScrapeTimedOut);
        parcel.writeInt(leecherCount);
        parcel.writeLong(nextAnnounceTime);
        parcel.writeLong(nextScrapeTime);
        parcel.writeString(scrape);
        parcel.writeInt(scrapeState);
        parcel.writeInt(seederCount);
        parcel.writeInt(tier);
    }
}


// TrackerStats' announceState
//Torrent._TrackerInactive       = 0;
//Torrent._TrackerWaiting        = 1;
//Torrent._TrackerQueued         = 2;
//Torrent._TrackerActive         = 3;