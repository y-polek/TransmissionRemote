package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.transport.Torrent;
import net.yupol.transmissionremote.app.transport.response.Response;
import net.yupol.transmissionremote.app.transport.response.UpdateTorrentsResponse;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class UpdateTorrentsRequest extends BaseRequest {

    private static final String TAG = UpdateTorrentsRequest.class.getName();

    private static final String[] TORRENT_METADATA = {
            Torrent.Metadata.ID,
            Torrent.Metadata.NAME,
            Torrent.Metadata.PERCENT_DONE,
            Torrent.Metadata.TOTAL_SIZE,
            Torrent.Metadata.ADDED_DATE,
            Torrent.Metadata.STATUS
    };

    public UpdateTorrentsRequest() {
        super("torrent-get");
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

    @Override
    public Response responseWrapper(HttpResponse httpResponse) {
        return new UpdateTorrentsResponse(httpResponse);
    }
}
