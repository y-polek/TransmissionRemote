package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public class AddTorrentByUrlRequest extends AddTorrentRequest {

    private static final String TAG = AddTorrentByUrlRequest.class.getSimpleName();

    private String url;

    public AddTorrentByUrlRequest(@Nonnull final String url, String destination, boolean paused) {
        super(destination, paused);

        if (url.matches("^[0-9a-fA-F]{40}$"))
            this.url = "magnet:?xt=urn:btih:"+url;
        else {
            this.url = url;
        }
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = super.getArguments();
        try {
            args.put("filename", url);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object");
        }
        return args;
    }
}
