package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.json.Torrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class GetTorrentsRequest extends Request<Torrent[]> {

    private static final String TAG = GetTorrentsRequest.class.getSimpleName();

    private static final String[] TORRENT_METADATA = {
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.ID,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.NAME,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.PERCENT_DONE,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.TOTAL_SIZE,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.ADDED_DATE,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.STATUS,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.RATE_DOWNLOAD,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.RATE_UPLOAD,
            net.yupol.transmissionremote.app.transport.Torrent.Metadata.LEFT_UNTIL_DONE
    };

    public GetTorrentsRequest() {
        super(Torrent[].class);
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected String getArguments() {
        try {
            return new JSONObject().put("fields", new JSONArray(Arrays.asList(TORRENT_METADATA))).toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }
    }
}
