package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseRequest implements Request {

    private static final String HEADER_SESSION_ID = "X-Transmission-Session-Id";

    private static final String TAG = BaseRequest.class.getName();

    private String method;
    private String sessionId;

    public BaseRequest(String method) {
        this.method = checkNotNull(method, "Method must be not null");
    }

    @Override
    public JSONObject getBody() {
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("method", method);
            JSONObject argumentsObj = getArguments();
            bodyObj.putOpt("arguments", argumentsObj);
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json body", e);
        }
        return bodyObj;
    }

    @Override
    public Header[] getHeaders() {
        return new Header[] {
                new BasicHeader(HTTP.CONTENT_TYPE, "json"),
                new BasicHeader(HEADER_SESSION_ID, sessionId)
        };
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    protected abstract JSONObject getArguments();
}
