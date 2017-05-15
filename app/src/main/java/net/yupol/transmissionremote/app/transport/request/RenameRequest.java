package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class RenameRequest extends TorrentActionRequest {

    private static final String TAG = RenameRequest.class.getSimpleName();

    private String path;
    private String name;

    public RenameRequest(int torrentId, String path, String name) {
        super("torrent-rename-path", torrentId);
        this.path = path;
        this.name = name;
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = super.getArguments();
        try {
            args.put("path", path);
            args.put("name", name);
        } catch (JSONException e) {
            Log.e(TAG, "Can't create arguments JSON object", e);
        }
        return args;
    }
}
