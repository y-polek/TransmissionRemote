package net.yupol.transmissionremote.app.model;

import android.support.annotation.NonNull;

public enum TrackerState {

    INACTIVE(0),
    WAITING (1),
    QUEUED  (2),
    ACTIVE  (3),
    UNKNOWN (-1);

    public final int code;

    TrackerState(int code) {
        this.code = code;
    }

    @NonNull
    public static TrackerState fromCode(int code) {
        for (TrackerState state : values()) {
            if (state.code == code) return state;
        }
        return UNKNOWN;
    }
}
