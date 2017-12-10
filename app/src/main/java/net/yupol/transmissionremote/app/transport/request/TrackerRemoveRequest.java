package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerRemoveRequest extends Request<Void> {

    private static final String TAG = TrackerRemoveRequest.class.getSimpleName();
    private int torrentId;
    private int trackerId;

    public TrackerRemoveRequest(int torrentId, int trackerId) {
        super(Void.class);
        this.torrentId = torrentId;
        this.trackerId = trackerId;
    }

    @Override
    protected String getMethod() {
        return "torrent-set";
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();
        try {
            args.put("ids", new JSONArray().put(torrentId));
            args.put("trackerRemove", new JSONArray().put(trackerId));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }

        return args;
    }
}
