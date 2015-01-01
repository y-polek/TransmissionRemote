package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.yupol.transmissionremote.app.server.Server;

import org.apache.commons.io.IOUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.annotation.Nonnull;

public abstract class Request<RESULT> extends GoogleHttpClientSpiceRequest<RESULT> {

    private static final String TAG = Request.class.getSimpleName();

    private static final String HEADER_SESSION_ID = "X-Transmission-Session-Id";
    private static final String URL_ENDING = "transmission/rpc";

    private Server server;
    private String sessionId;

    private int statusCode = -1;
    private String responseBody;

    public Request(Class<RESULT> resultClass) {
        super(resultClass);
    }

    public void setServer(@Nonnull Server server) {
        this.server = server;
    }

    public void setSessionId(@Nonnull String sessionId) {
        this.sessionId = sessionId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public RESULT loadDataFromNetwork() throws Exception {
        if (server == null || sessionId == null) {
            throw new IllegalStateException("Server and sessionId must be set before executing");
        }

        String url = "http://" + server.getHost() + ':' + server.getPort() + "/" + URL_ENDING;

        HttpRequest request = getHttpRequestFactory().buildGetRequest(new GenericUrl(url));

        HttpHeaders headers = new HttpHeaders()
                .set(HTTP.CONTENT_TYPE, "json")
                .set(HEADER_SESSION_ID, sessionId);
        request.setHeaders(headers);

        request.setContent(new ByteArrayContent("application/json", createBody().getBytes()));

        request.setParser(new JacksonFactory().createJsonObjectParser());
        HttpResponse response = null;
        try {
            response = request.execute();
        } finally {
            if (response != null) {
                statusCode = response.getStatusCode();
                try {
                    responseBody = IOUtils.toString(response.getContent());
                } catch (IOException e) {
                    responseBody = null;
                }
            } else {
                statusCode = -1;
                responseBody = null;
            }
        }
        return response.parseAs(getResultType());
    }

    private String createBody() {
        JSONObject bodyObj = new JSONObject();
        try {
            bodyObj.put("method", getMethod());
            bodyObj.putOpt("arguments", getArguments());
        } catch (JSONException e) {
            Log.e(TAG, "Error while creating json body", e);
        }
        return bodyObj.toString();
    }

    protected abstract String getMethod();

    protected abstract String getArguments();
}
