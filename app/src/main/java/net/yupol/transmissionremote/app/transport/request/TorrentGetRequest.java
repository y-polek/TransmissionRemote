package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.model.TorrentMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class TorrentGetRequest extends Request<Torrents> {

    private static final String TAG = TorrentGetRequest.class.getSimpleName();

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
            TorrentMetadata.UPLOADED_EVER,
            TorrentMetadata.UPLOAD_RATIO,
            TorrentMetadata.ERROR,
            TorrentMetadata.ERROR_STRING,
            TorrentMetadata.IS_FINISHED,
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
    };

    private int[] ids;

    public TorrentGetRequest() {
        super(Torrents.class);
    }

    public TorrentGetRequest(int... ids) {
        this();
        this.ids = ids;
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected JSONObject getArguments() {
        try {
            JSONObject args = new JSONObject().put("fields", new JSONArray(Arrays.asList(TORRENT_METADATA)));

            if (ids != null) {
                JSONArray idArray = new JSONArray();
                for (int id : ids) {
                    idArray.put(id);
                }
                args.put("ids", idArray);
            }

            return args;
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }
    }
}
