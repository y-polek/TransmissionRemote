package net.yupol.transmissionremote.app.transport.response;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import static net.yupol.transmissionremote.app.preferences.ServerPreferences.*;

public class SessionGetResponse extends Response {

    private int speedLimitDown;
    private boolean speedLimitDownEnabled;
    private int speedLimitUp;
    private boolean speedLimitUpEnabled;
    private int altSpeedDown;
    private int altSpeedUp;
    private boolean altSpeedEnabled;

    public SessionGetResponse(HttpResponse response) {
        super(response);

        JSONObject args = getArguments();
        speedLimitDown = args.optInt(SPEED_LIMIT_DOWN);
        speedLimitDownEnabled = args.optBoolean(SPEED_LIMIT_DOWN_ENABLED);
        speedLimitUp = args.optInt(SPEED_LIMIT_UP);
        speedLimitUpEnabled = args.optBoolean(SPEED_LIMIT_UP_ENABLED);
        altSpeedDown = args.optInt(ALT_SPEED_LIMIT_DOWN);
        altSpeedUp = args.optInt(ALT_SPEED_LIMIT_UP);
        altSpeedEnabled = args.optBoolean(ALT_SPEED_LIMIT_ENABLED);
    }

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

    @Override
    public String toString() {
        return "SessionGetResponse {" +
                "speedLimitDown=" + speedLimitDown +
                ", speedLimitDownEnabled=" + speedLimitDownEnabled +
                ", speedLimitUp=" + speedLimitUp +
                ", speedLimitUpEnabled=" + speedLimitUpEnabled +
                ", altSpeedDown=" + altSpeedDown +
                ", altSpeedUp=" + altSpeedUp +
                ", altSpeedEnabled=" + altSpeedEnabled +
                '}';
    }
}
