package net.yupol.transmissionremote.model.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import net.yupol.transmissionremote.data.api.model.ServerSettingsEntity;

public class ServerSettings implements Parcelable {

    public static final String SPEED_LIMIT_DOWN = "speed-limit-down";
    public static final String SPEED_LIMIT_DOWN_ENABLED = "speed-limit-down-enabled";
    public static final String SPEED_LIMIT_UP = "speed-limit-up";
    public static final String SPEED_LIMIT_UP_ENABLED = "speed-limit-up-enabled";
    public static final String ALT_SPEED_LIMIT_DOWN = "alt-speed-down";
    public static final String ALT_SPEED_LIMIT_UP = "alt-speed-up";
    public static final String ALT_SPEED_LIMIT_ENABLED = "alt-speed-enabled";
    public static final String DOWNLOAD_DIR = "download-dir";
    public static final String SEED_RATIO_LIMITED = "seedRatioLimited";
    public static final String SEED_RATIO_LIMIT = "seedRatioLimit";
    public static final String SEED_IDLE_LIMITED = "idle-seeding-limit-enabled";
    public static final String SEED_IDLE_LIMIT = "idle-seeding-limit";
    public static final String DOWNLOAD_DIR_FREE_SPACE = "download-dir-free-space";
    public static final String VERSION = "version";

    @Json(name = SPEED_LIMIT_DOWN)
    private int speedLimitDown;

    @Json(name = SPEED_LIMIT_DOWN_ENABLED)
    private boolean speedLimitDownEnabled;

    @Json(name = SPEED_LIMIT_UP)
    private int speedLimitUp;

    @Json(name = SPEED_LIMIT_UP_ENABLED)
    private boolean speedLimitUpEnabled;

    @Json(name = ALT_SPEED_LIMIT_DOWN)
    private int altSpeedLimitDown;

    @Json(name = ALT_SPEED_LIMIT_UP)
    private int altSpeedLimitUp;

    @Json(name = ALT_SPEED_LIMIT_ENABLED)
    private boolean altSpeedLimitEnabled;

    @Json(name = DOWNLOAD_DIR)
    private String downloadDir;

    @Json(name = SEED_RATIO_LIMITED)
    private boolean seedRatioLimited;

    @Json(name = SEED_RATIO_LIMIT)
    private double seedRatioLimit;

    @Json(name = SEED_IDLE_LIMITED)
    private boolean seedIdleLimited;

    @Json(name = SEED_IDLE_LIMIT)
    private int seedIdleLimit;

    @Json(name = DOWNLOAD_DIR_FREE_SPACE)
    private long downloadDirFreeSpace;

    @Json(name = VERSION)
    private String version;

    public ServerSettings() {
        // accessible default constructor required for JSON parser
    }

    protected ServerSettings(Parcel in) {
        speedLimitDown = in.readInt();
        speedLimitDownEnabled = in.readByte() != 0;
        speedLimitUp = in.readInt();
        speedLimitUpEnabled = in.readByte() != 0;
        altSpeedLimitDown = in.readInt();
        altSpeedLimitUp = in.readInt();
        altSpeedLimitEnabled = in.readByte() != 0;
        downloadDir = in.readString();
        seedRatioLimited = in.readByte() != 0;
        seedRatioLimit = in.readDouble();
        seedIdleLimited = in.readByte() != 0;
        seedIdleLimit = in.readInt();
        downloadDirFreeSpace = in.readLong();
        version = in.readString();
    }

    public static final Creator<ServerSettings> CREATOR = new Creator<ServerSettings>() {
        @Override
        public ServerSettings createFromParcel(Parcel in) {
            return new ServerSettings(in);
        }

        @Override
        public ServerSettings[] newArray(int size) {
            return new ServerSettings[size];
        }
    };

    public int getSpeedLimitDown() {
        return speedLimitDown;
    }

    public boolean isSpeedLimitDownEnabled() {
        return speedLimitDownEnabled;
    }

    public int getSpeedLimitUp() {
        return speedLimitUp;
    }

    public boolean isSpeedLimitUpEnabled() {
        return speedLimitUpEnabled;
    }

    public int getAltSpeedLimitDown() {
        return altSpeedLimitDown;
    }

    public int getAltSpeedLimitUp() {
        return altSpeedLimitUp;
    }

    public boolean isAltSpeedLimitEnabled() {
        return altSpeedLimitEnabled;
    }

    public String getDownloadDir() {
        return downloadDir;
    }

    public boolean isSeedRatioLimited() {
        return seedRatioLimited;
    }

    public double getSeedRatioLimit() {
        return seedRatioLimit;
    }

    public boolean isSeedIdleLimited() {
        return seedIdleLimited;
    }

    public int getSeedIdleLimit() {
        return seedIdleLimit;
    }

    public long getDownloadDirFreeSpace() {
        return downloadDirFreeSpace;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "ServerSettings{" +
                "speedLimitDown=" + speedLimitDown +
                ", speedLimitDownEnabled=" + speedLimitDownEnabled +
                ", speedLimitUp=" + speedLimitUp +
                ", speedLimitUpEnabled=" + speedLimitUpEnabled +
                ", altSpeedLimitDown=" + altSpeedLimitDown +
                ", altSpeedLimitUp=" + altSpeedLimitUp +
                ", altSpeedLimitEnabled=" + altSpeedLimitEnabled +
                ", downloadDir='" + downloadDir + '\'' +
                ", seedRatioLimited=" + seedRatioLimited +
                ", seedRatioLimit=" + seedRatioLimit +
                ", seedIdleLimited=" + seedIdleLimited +
                ", seedIdleLimit=" + seedIdleLimit +
                ", downloadDirFreeSpace=" + downloadDirFreeSpace +
                ", version=" + version +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(speedLimitDown);
        dest.writeByte((byte) (speedLimitDownEnabled ? 1 : 0));
        dest.writeInt(speedLimitUp);
        dest.writeByte((byte) (speedLimitUpEnabled ? 1 : 0));
        dest.writeInt(altSpeedLimitDown);
        dest.writeInt(altSpeedLimitUp);
        dest.writeByte((byte) (altSpeedLimitEnabled ? 1 : 0));
        dest.writeString(downloadDir);
        dest.writeByte((byte) (seedRatioLimited ? 1 : 0));
        dest.writeDouble(seedRatioLimit);
        dest.writeByte((byte) (seedIdleLimited ? 1 : 0));
        dest.writeInt(seedIdleLimit);
        dest.writeLong(downloadDirFreeSpace);
        dest.writeString(version);
    }

    public static ServerSettings fromEntity(ServerSettingsEntity entity) {
        ServerSettings settings = new ServerSettings();
        settings.speedLimitDown = entity.speedLimitDown;
        settings.speedLimitDownEnabled = entity.speedLimitDownEnabled;
        settings.speedLimitUp = entity.speedLimitUp;
        settings.speedLimitUpEnabled = entity.speedLimitUpEnabled;
        settings.altSpeedLimitDown = entity.altSpeedLimitDown;
        settings.altSpeedLimitUp = entity.altSpeedLimitUp;
        settings.altSpeedLimitEnabled = entity.altSpeedLimitEnabled;
        settings.downloadDir = entity.downloadDir;
        settings.seedRatioLimited = entity.seedRatioLimited;
        settings.seedRatioLimit = entity.seedRatioLimit;
        settings.seedIdleLimited = entity.seedIdleLimited;
        settings.seedIdleLimit = entity.seedIdleLimit;
        settings.downloadDirFreeSpace = entity.downloadDirFreeSpace;
        settings.version = entity.version;
        return settings;
    }
}
