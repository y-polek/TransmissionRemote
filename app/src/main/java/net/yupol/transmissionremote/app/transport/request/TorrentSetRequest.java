package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TorrentSetRequest extends Request<Void> {

    private static final String TAG = TorrentSetRequest.class.getSimpleName();

    private int torrentId;
    boolean isWanted;
    private List<Integer> fileIndices = new LinkedList<>();

    public TorrentSetRequest(int torrentId, boolean isWanted, int... fileIndices) {
        super(Void.class);
        this.torrentId = torrentId;
        this.isWanted = isWanted;
        for (int index : fileIndices) {
            this.fileIndices.add(index);
        }
    }

    @Override
    protected String getMethod() {
        return "torrent-set";
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();
        try {
            args.put("ids", new JSONArray(Collections.singleton(torrentId)));
            args.put(isWanted ? "files-wanted" : "files-unwanted", new JSONArray(fileIndices));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating JSON object");
        }
        return args;
    }
}
