package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.TorrentMetadata;
import net.yupol.transmissionremote.app.model.json.Torrents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class TorrentGetRequest extends Request<Torrents> {

    private static final String TAG = TorrentGetRequest.class.getSimpleName();

    private static final List<String> FIELD_KEYS = Arrays.asList(
            TorrentMetadata.ID,
            TorrentMetadata.NAME,
            TorrentMetadata.PERCENT_DONE,
            TorrentMetadata.TOTAL_SIZE,
            TorrentMetadata.ADDED_DATE,
            TorrentMetadata.STATUS,
            TorrentMetadata.RATE_DOWNLOAD,
            TorrentMetadata.RATE_UPLOAD,
            TorrentMetadata.UPLOADED_EVER,
            TorrentMetadata.UPLOAD_RATIO,
            TorrentMetadata.ETA,
            TorrentMetadata.ERROR,
            TorrentMetadata.ERROR_STRING,
            TorrentMetadata.IS_FINISHED,
            TorrentMetadata.SIZE_WHEN_DONE,
            TorrentMetadata.LEFT_UNTIL_DONE,
            TorrentMetadata.PEERS_GETTING_FROM_US,
            TorrentMetadata.PEERS_SENDING_TO_US,
            TorrentMetadata.WEBSEEDS_SENDING_TO_US,
            TorrentMetadata.QUEUE_POSITION,
            TorrentMetadata.RECHECK_PROGRESS
    );

    private JSONObject args = new JSONObject();

    public TorrentGetRequest() {
        super(Torrents.class);
        try {
            args = new JSONObject().put("fields", new JSONArray(FIELD_KEYS));
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            throw new RuntimeException(e);
        }
    }

    public TorrentGetRequest(int... ids) {
        this();

        JSONArray idsArray = new JSONArray();
        for (int id : ids) {
            idsArray.put(id);
        }
        try {
            args.put("ids", idsArray);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getMethod() {
        return "torrent-get";
    }

    @Override
    protected JSONObject getArguments() {
        return args;
    }
}
