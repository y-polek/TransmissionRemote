package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import net.yupol.transmissionremote.data.api.model.TrackerEntity;
import net.yupol.transmissionremote.data.api.model.TrackerStatsEntity;

public class TrackerStats implements Parcelable {

    @Json(name = "id") public int id;
    @Json(name = "announce") public String announce;
    @Json(name = "announceState") public int announceState;
    @Json(name = "downloadCount") public int downloadCount;
    @Json(name = "hasAnnounced") public boolean hasAnnounced;
    @Json(name = "hasScraped") public boolean hasScraped;
    @Json(name = "host") public String host;
    @Json(name = "isBackup") public boolean isBackup;
    @Json(name = "lastAnnouncePeerCount") public int lastAnnouncePeerCount;
    @Json(name = "lastAnnounceResult") public String lastAnnounceResult;
    @Json(name = "lastAnnounceStartTime") public long lastAnnounceStartTime;
    @Json(name = "lastAnnounceSucceeded") public boolean lastAnnounceSucceeded;
    @Json(name = "lastAnnounceTime") public long lastAnnounceTime;
    @Json(name = "lastAnnounceTimedOut") public boolean lastAnnounceTimedOut;
    @Json(name = "lastScrapeResult") public String lastScrapeResult;
    @Json(name = "lastScrapeStartTime") public long lastScrapeStartTime;
    @Json(name = "lastScrapeSucceeded") public boolean lastScrapeSucceeded;
    @Json(name = "lastScrapeTime") public long lastScrapeTime;
    @Json(name = "lastScrapeTimedOut") public byte lastScrapeTimedOut;
    @Json(name = "leecherCount") public int leecherCount;
    @Json(name = "nextAnnounceTime") public long nextAnnounceTime;
    @Json(name = "nextScrapeTime") public long nextScrapeTime;
    @Json(name = "scrape") public String scrape;
    @Json(name = "scrapeState") public int scrapeState;
    @Json(name = "seederCount") public int seederCount;
    @Json(name = "tier") public int tier;

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

    public static TrackerStats fromEntity(TrackerStatsEntity entity) {
        TrackerStats stats = new TrackerStats();
        stats.id = entity.id;
        stats.announce = entity.announce;
        stats.announceState = entity.announceState;
        stats.downloadCount = entity.downloadCount;
        stats.hasAnnounced = entity.hasAnnounced;
        stats.hasScraped = entity.hasScraped;
        stats.host = entity.host;
        stats.isBackup = entity.isBackup;
        stats.lastAnnouncePeerCount = entity.lastAnnouncePeerCount;
        stats.lastAnnounceResult = entity.lastAnnounceResult;
        stats.lastAnnounceStartTime = entity.lastAnnounceStartTime;
        stats.lastAnnounceSucceeded = entity.lastAnnounceSucceeded;
        stats.lastAnnounceTime = entity.lastAnnounceTime;
        stats.lastAnnounceTimedOut = entity.lastAnnounceTimedOut;
        stats.lastScrapeResult = entity.lastScrapeResult;
        stats.lastScrapeStartTime = entity.lastScrapeStartTime;
        stats.lastScrapeSucceeded = entity.lastScrapeSucceeded;
        stats.lastScrapeTime = entity.lastScrapeTime;
        stats.lastScrapeTimedOut = entity.lastScrapeTimedOut;
        stats.leecherCount = entity.leecherCount;
        stats.nextAnnounceTime = entity.nextAnnounceTime;
        stats.nextScrapeTime = entity.nextScrapeTime;
        stats.scrape = entity.scrape;
        stats.scrapeState = entity.scrapeState;
        stats.seederCount = entity.seederCount;
        stats.tier = entity.tier;
        return stats;
    }
}


// TrackerStats' announceState
//Torrent._TrackerInactive       = 0;
//Torrent._TrackerWaiting        = 1;
//Torrent._TrackerQueued         = 2;
//Torrent._TrackerActive         = 3;