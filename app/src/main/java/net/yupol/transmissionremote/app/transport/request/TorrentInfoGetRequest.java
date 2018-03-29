package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.model.TorrentMetadata;
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
            TorrentMetadata.SEED_IDLE_MODE,

            TorrentMetadata.HAVE_UNCHECKED,
            TorrentMetadata.HAVE_VALID,
            TorrentMetadata.SIZE_WHEN_DONE,
            TorrentMetadata.LEFT_UNTIL_DONE,
            TorrentMetadata.DESIRED_AVAILABLE,
            TorrentMetadata.PIECE_COUNT,
            TorrentMetadata.PIECE_SIZE,
            TorrentMetadata.DOWNLOAD_DIR,
            TorrentMetadata.IS_PRIVATE,
            TorrentMetadata.CREATOR,
            TorrentMetadata.DATE_CREATED,
            TorrentMetadata.COMMENT,
            TorrentMetadata.DOWNLOAD_EVER,
            TorrentMetadata.CORRUPT_EVER,
            TorrentMetadata.UPLOADED_EVER,
            TorrentMetadata.ADDED_DATE,
            TorrentMetadata.ACTIVITY_DATE,
            TorrentMetadata.SECONDS_DOWNLOADING,
            TorrentMetadata.SECONDS_SEEDING,
            TorrentMetadata.PEERS,
            TorrentMetadata.TRACKERS,
            TorrentMetadata.TRACKER_STATS
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
