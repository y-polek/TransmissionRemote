package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.*;

public class Response {

    private static final String TAG = Response.class.getName();

    private static final String HEADER_SESSION_ID = "X-Transmission-Session-Id";

    protected HttpResponse response;

    private String body;

    public Response(HttpResponse response) {
        this.response = checkNotNull(response, "HttpResponse must be not null");
        body = readBody();
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
}
