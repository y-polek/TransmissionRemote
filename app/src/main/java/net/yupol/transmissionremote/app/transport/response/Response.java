package net.yupol.transmissionremote.app.transport.response;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class Response {

    private static final String TAG = Response.class.getSimpleName();

    private static final String HEADER_SESSION_ID = "X-Transmission-Session-Id";
    private static final String SUCCESS_STRING = "success";

    protected HttpResponse response;

    private String body;
    private JSONObject jsonBody;
    private JSONObject arguments;
    private boolean result;

    public Response(HttpResponse response) {
        this.response = checkNotNull(response, "HttpResponse must be not null");
        body = readBody();
        try {
            jsonBody = new JSONObject(body);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse response body: '" + body + "'");
        }
        arguments = parseArguments();
        result = parseResult();
    }

    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public String getSessionId() {
        Header header = response.getFirstHeader(HEADER_SESSION_ID);
        return header != null ? header.getValue() : null;
    }

    public String getBody() {
        return body;
    }

    public boolean getResult() {
        return result;
    }

    protected JSONObject getJsonBody() {
        return jsonBody;
    }

    protected JSONObject getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status code=" + getStatusCode() +
                ", result=" + result +
                ", body='" + body + '\'' +
                '}';
    }

    private String readBody() {
        Header encodingHeader = response.getEntity().getContentEncoding();
        String encoding = encodingHeader != null ? encodingHeader.getValue() : "UTF-8";
        try {
            InputStream is = response.getEntity().getContent();
            return IOUtils.toString(is, encoding);
        } catch (IOException e) {
            Log.e(TAG, "Could not read response content", e);
            return null;
        }
    }

    private JSONObject parseArguments() {
        if (jsonBody == null)
            return new JSONObject();
        try {
            return jsonBody.getJSONObject("arguments");
        } catch (JSONException e) {
            Log.d(TAG, "Can't find 'arguments' array in response body: '" + jsonBody + "'");
            return new JSONObject();
        }
    }

    private boolean parseResult() {
        if (jsonBody == null) {
            return false;
        }
        return "success".equalsIgnoreCase(jsonBody.optString("result"));
    }
}
