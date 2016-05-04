package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.TorrentMetadata;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class TorrentInfoGetRequest extends Request<TorrentInfo> {

    private static final String TAG = TorrentInfoGetRequest.class.getSimpleName();

    private static final List<String> FIELD_KEYS = Arrays.asList(
            TorrentMetadata.ID,
            TorrentMetadata.FILES,
            TorrentMetadata.FILE_STATS,
            TorrentMetadata.BANDWIDTH_PRIORITY,
            TorrentMetadata.HONORS_SESSION_LIMITS,
            TorrentMetadata.DOWNLOAD_LIMITED,
            TorrentMetadata.DOWNLOAD_LIMIT,
            TorrentMetadata.UPLOAD_LIMITED,
            TorrentMetadata.UPLOAD_LIMIT,
            TorrentMetadata.SEED_RATIO_LIMIT,
            TorrentMetadata.SEED_RATIO_MODE,
            TorrentMetadata.SEED_IDLE_LIMIT,
            TorrentMetadata.SEED_IDLE_MODE
    );

    private int id;

    public TorrentInfoGetRequest(int id) {
        super(TorrentInfo.class);
        this.id = id;
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected JSONObject getArguments() {
        try {
            return new JSONObject()
                    .put("ids", new JSONArray().put(id))
                    .put("fields", new JSONArray(FIELD_KEYS));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }
    }
}
