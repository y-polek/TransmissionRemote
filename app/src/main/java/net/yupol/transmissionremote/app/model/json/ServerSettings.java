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
                ", downloadDir=" + downloadDir +
                '}';
    }
}
