package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TorrentRemoveRequest extends Request<Void> {

    private static final String TAG = TorrentRemoveRequest.class.getSimpleName();
    private int[] ids;
    private boolean deleteLocalData;

    public TorrentRemoveRequest(int[] ids, boolean deleteLocalData) {
        super(Void.class);
        this.ids = ids;
        this.deleteLocalData = deleteLocalData;
    }

    @Override
    protected String getMethod() {
        return "torrent-remove";
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();

        JSONArray idArray = new JSONArray();
        for (int id : ids) {
            idArray.put(id);
        }
        try {
            args.put("ids", idArray);
            args.put("delete-local-data", deleteLocalData);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }

        return args;
    }
}
