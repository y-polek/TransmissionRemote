package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerAddRequest extends Request<Void> {

    private static final String TAG = TrackerAddRequest.class.getSimpleName();
    private int torrentId;
    private String url;

    public TrackerAddRequest(int torrentId, String url) {
        super(Void.class);
        this.torrentId = torrentId;
        this.url = url;
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
            args.put("trackerAdd", new JSONArray().put(url));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }

        return args;
    }
}
