package net.yupol.transmissionremote.app.transport.request;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SetLocationRequest extends TorrentActionRequest {

    private static final String TAG = SetLocationRequest.class.getSimpleName();

    private String location;
    private boolean moveData;

    public SetLocationRequest(String location, boolean moveData, int... torrentIds) {
        super("torrent-set-location", torrentIds);
        this.location = location;
        this.moveData = moveData;
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = super.getArguments();
        try {
            args.put("location", location);
            args.put("move", moveData);
        } catch (JSONException e) {
            Log.e(TAG, "Can't create arguments JSON object", e);
        }
        return args;
    }
}
