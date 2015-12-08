package net.yupol.transmissionremote.app.transport.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionSetRequest extends Request<Void> implements Parcelable {

    private static final String TAG = SessionSetRequest.class.getSimpleName();

    private JSONObject arguments;

    public SessionSetRequest(JSONObject arguments) {
        super(Void.class);
        this.arguments = arguments;
    }

    @Override
    protected String getMethod() {
        return "session-set";
    }

    @Override
    protected JSONObject getArguments() {
        return arguments;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(arguments.toString());
    }

    public static final Creator<SessionSetRequest> CREATOR = new Creator<SessionSetRequest>() {
        @Override
        public SessionSetRequest createFromParcel(Parcel in) {
            String argsStr = in.readString();
            try {
                return new SessionSetRequest(new JSONObject(argsStr));
            } catch (JSONException e) {
                Log.e(TAG, "Failed to restore from parcel. Args string: " + argsStr, e);
                return new SessionSetRequest(new JSONObject());
            }
        }

        @Override
        public SessionSetRequest[] newArray(int size) {
            return new SessionSetRequest[size];
        }
    };

    public static class Builder implements RequestBuilder<SessionSetRequest> {

        private Boolean altSpeedLimitEnabled;
        private Long altSpeedLimitDown;
        private Long altSpeedLimitUp;
        private Boolean speedLimitDownEnabled;
        private Long speedLimitDown;
        private Boolean speedLimitUpEnabled;
        private Long speedLimitUp;

        private boolean changed = false;

        public Builder setAltSpeedLimitEnabled(Boolean enabled) {
            altSpeedLimitEnabled = enabled;
            return changedBuilder();
        }

        public Builder setAltSpeedLimitDown(Long limit) {
            altSpeedLimitDown = limit;
            return changedBuilder();
        }

        public Builder setAltSpeedLimitUp(Long limit) {
            altSpeedLimitUp = limit;
            return changedBuilder();
        }

        public Builder setSpeedLimitDownEnabled(Boolean enabled) {
            speedLimitDownEnabled = enabled;
            return changedBuilder();
        }

        public Builder setSpeedLimitDown(Long limit) {
            speedLimitDown = limit;
            return changedBuilder();
        }

        public Builder setSpeedLimitUpEnabled(Boolean enabled) {
            speedLimitUpEnabled = enabled;
            return changedBuilder();
        }

        public Builder setSpeedLimitUp(Long limit) {
            this.speedLimitUp = limit;
            return changedBuilder();
        }

        private Builder changedBuilder() {
            changed = true;
            return this;
        }

        public boolean isChanged() {
            return changed;
        }

        @Override
        public SessionSetRequest build() {
            JSONObject args = new JSONObject();
            try {
                if (altSpeedLimitEnabled != null) {
                    args.put("alt-speed-enabled", altSpeedLimitEnabled);
                }
                if (altSpeedLimitDown != null) {
                    args.put("alt-speed-down", altSpeedLimitDown);
                }
                if (altSpeedLimitUp != null) {
                    args.put("alt-speed-up", altSpeedLimitUp);
                }
                if (speedLimitDownEnabled != null) {
                    args.put("speed-limit-down-enabled", speedLimitDownEnabled);
                }
                if (speedLimitDown != null) {
                    args.put("speed-limit-down", speedLimitDown);
                }
                if (speedLimitUpEnabled != null) {
                    args.put("speed-limit-up-enabled", speedLimitUpEnabled);
                }
                if (speedLimitUp != null) {
                    args.put("speed-limit-up", speedLimitUp);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error while creating JSON object");
            }
            return new SessionSetRequest(args);
        }
    }
}
