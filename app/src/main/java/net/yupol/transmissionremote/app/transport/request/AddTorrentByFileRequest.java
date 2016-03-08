package net.yupol.transmissionremote.app.transport.request;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class AddTorrentByFileRequest extends AddTorrentRequest {

    private static final String TAG = AddTorrentByFileRequest.class.getSimpleName();

    private String metaInfo;

    public AddTorrentByFileRequest(byte[] torrentFileContent, String destination, boolean paused) {
        super(destination, paused);
        metaInfo = Base64.encodeToString(torrentFileContent, Base64.DEFAULT);
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = super.getArguments();
        try {
            args.put("metainfo", metaInfo);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object");
        }
        return args;
    }
}
