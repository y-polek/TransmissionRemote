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
    private Collection<Torrent> torrents;

    public TorrentActionRequest(String method, Collection<Torrent> torrents) {
        super(Void.class);
        this.method = method;
        this.torrents = torrents;
    }

    @Override
    protected String getMethod() {
        return method;
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();

        JSONArray torrentIds = new JSONArray();
        for (Torrent torrent : torrents) {
            torrentIds.put(torrent.getId());
        }

        try {
            args.put("ids", torrentIds);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to form arguments JSON object for '" + getMethod() + "' request", e);
        }
        return args;
    }

    protected Collection<Torrent> getTorrents() {
        return torrents;
    }
}
