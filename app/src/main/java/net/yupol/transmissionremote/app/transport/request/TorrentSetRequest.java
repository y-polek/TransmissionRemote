package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

public class TorrentSetRequest extends Request<Void> {

    private static final String TAG = TorrentSetRequest.class.getSimpleName();

    private JSONObject arguments;

    public TorrentSetRequest(JSONObject arguments) {
        super(Void.class);
        this.arguments = arguments;
    }

    @Override
    protected String getMethod() {
        return "torrent-set";
    }

    @Override
    protected JSONObject getArguments() {
        return arguments;
    }

    public static Builder builder(int torrentId) {
        return new Builder(torrentId);
    }

    public static class Builder {

        private int torrentId;
        private int[] filesWantedIndices;
        private int[] filesUnwantedIndices;

        private Builder(int torrentId) {
            this.torrentId = torrentId;
        }

        public Builder filesWanted(int... fileIndices) {
            filesWantedIndices = fileIndices;
            return this;
        }

        public Builder filesUnwanted(int... fileIndices) {
            filesUnwantedIndices = fileIndices;
            return this;
        }

        public TorrentSetRequest build() {
            JSONObject args = new JSONObject();
            try {
                args.put("ids", new JSONArray(Collections.singleton(torrentId)));
                if (filesWantedIndices != null && filesWantedIndices.length > 0) {
                    args.put("files-wanted", new JSONArray(ArrayUtils.toObject(filesWantedIndices)));
                }
                if (filesUnwantedIndices != null && filesUnwantedIndices.length > 0) {
                    args.put("files-unwanted", new JSONArray(ArrayUtils.toObject(filesUnwantedIndices)));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error while creating JSON object");
            }
            return new TorrentSetRequest(args);
        }
    }
}
