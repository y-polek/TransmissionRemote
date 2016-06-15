package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.json.Torrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public abstract class TorrentActionRequest extends Request<Void> {

    private static final String TAG = TorrentActionRequest.class.getSimpleName();

    private String method;
    private int[] torrentIds;

    public TorrentActionRequest(String method, int[] torrentIds) {
        super(Void.class);
        this.method = method;
        this.torrentIds = torrentIds;
    }

    @Override
    protected String getMethod() {
        return method;
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();

        JSONArray ids = new JSONArray();
        for (int id : torrentIds) {
            ids.put(id);
        }

        try {
            args.put("ids", ids);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to form arguments JSON object for '" + getMethod() + "' request", e);
        }
        return args;
    }

    public static int[] toIds(Collection<Torrent> torrents) {
        int[] ids = new int[torrents.size()];
        int i = 0;
        for (Torrent torrent : torrents) {
            ids[i++] = torrent.getId();
        }
        return ids;
    }
}
