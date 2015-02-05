package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.model.TorrentMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class GetTorrentsRequest extends Request<Torrents> {

    private static final String TAG = GetTorrentsRequest.class.getSimpleName();

    private static final String[] TORRENT_METADATA = {
            TorrentMetadata.ID,
            TorrentMetadata.NAME,
            TorrentMetadata.PERCENT_DONE,
            TorrentMetadata.TOTAL_SIZE,
            TorrentMetadata.ADDED_DATE,
            TorrentMetadata.STATUS,
            TorrentMetadata.RATE_DOWNLOAD,
            TorrentMetadata.RATE_UPLOAD,
            TorrentMetadata.LEFT_UNTIL_DONE,
            TorrentMetadata.ERROR,
            TorrentMetadata.ERROR_STRING
    };

    public GetTorrentsRequest() {
        super(Torrents.class);
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected JSONObject getArguments() {
        try {
            return new JSONObject().put("fields", new JSONArray(Arrays.asList(TORRENT_METADATA)));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }
    }
}
