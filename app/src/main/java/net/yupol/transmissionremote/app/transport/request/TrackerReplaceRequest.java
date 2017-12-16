package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerReplaceRequest extends Request<Void> {

    private static final String TAG = TrackerReplaceRequest.class.getSimpleName();
    private int torrentId;
    private int trackerId;
    private String url;

    public TrackerReplaceRequest(int torrentId, int trackerId, String url) {
        super(Void.class);
        this.torrentId = torrentId;
        this.trackerId = trackerId;
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
            args.put("trackerReplace", //new JSONArray().put(
                    new JSONArray()
                            .put(trackerId)
                            .put(url)
            //)
            );
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }

        return args;
    }
}
