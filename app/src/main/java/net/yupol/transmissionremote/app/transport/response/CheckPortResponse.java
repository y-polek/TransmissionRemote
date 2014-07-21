package net.yupol.transmissionremote.app.transport.response;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.json.JSONException;

public class CheckPortResponse extends Response {

    private static final String TAG = CheckPortResponse.class.getSimpleName();

    public CheckPortResponse(HttpResponse response) {
        super(response);
    }

    public boolean isOpen() {
        try {
            return getArguments().getBoolean("port-is-open");
        } catch (JSONException e) {
            Log.d(TAG, "Can't find 'port-is-open' field in response arguments: '" + getArguments() + "'");
            return false;
        }
    }
}
