package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import net.yupol.transmissionremote.app.model.json.FreeSpace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FreeSpaceRequest extends Request<FreeSpace> {

    private static final String TAG = FreeSpaceRequest.class.getSimpleName();

    private String path;

    public FreeSpaceRequest(String path) {
        super(FreeSpace.class);
        this.path = path;
    }

    @Override
    protected String getMethod() {
        return "free-space";
    }

    @Override
    protected JSONObject getArguments() {
        try {
            return new JSONObject().put("path", path);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json object", e);
            return null;
        }
    }
}
