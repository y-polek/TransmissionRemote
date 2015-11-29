package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

public class ServerSettings {

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

    @Key(SPEED_LIMIT_DOWN)
    private int speedLimitDown;

    @Key(SPEED_LIMIT_DOWN_ENABLED)
    private boolean speedLimitDownEnabled;

    @Key(SPEED_LIMIT_UP)
    private int speedLimitUp;

    @Key(SPEED_LIMIT_UP_ENABLED)
    private boolean speedLimitUpEnabled;

    @Key(ALT_SPEED_LIMIT_DOWN)
    private int altSpeedDown;

    @Key(ALT_SPEED_LIMIT_UP)
    private int altSpeedUp;

    @Key(ALT_SPEED_LIMIT_ENABLED)
    private boolean altSpeedEnabled;

    @Key(DOWNLOAD_DIR)
    private String downloadDir;

    @Key(SEED_RATIO_LIMITED)
    private boolean seedRatioLimited;

    @Key(SEED_RATIO_LIMIT)
    private double seedRatioLimit;

    @Key(SEED_IDLE_LIMITED)
    private boolean seedIdleLimited;

    @Key(SEED_IDLE_LIMIT)
    private int seedIdleLimit;

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

    public int getAltSpeedDown() {
        return altSpeedDown;
    }

    public int getAltSpeedUp() {
        return altSpeedUp;
    }

    public boolean isAltSpeedEnabled() {
        return altSpeedEnabled;
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

    @Override
    public String toString() {
        return "ServerSettings{" +
                "speedLimitDown=" + speedLimitDown +
                ", speedLimitDownEnabled=" + speedLimitDownEnabled +
                ", speedLimitUp=" + speedLimitUp +
                ", speedLimitUpEnabled=" + speedLimitUpEnabled +
                ", altSpeedDown=" + altSpeedDown +
                ", altSpeedUp=" + altSpeedUp +
                ", altSpeedEnabled=" + altSpeedEnabled +
                ", downloadDir='" + downloadDir + '\'' +
                ", seedRatioLimited=" + seedRatioLimited +
                ", seedRatioLimit=" + seedRatioLimit +
                ", seedIdleLimited=" + seedIdleLimited +
                ", seedIdleLimit=" + seedIdleLimit +
                '}';
    }
}
