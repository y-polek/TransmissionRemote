package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.yupol.transmissionremote.app.server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.Collections;

import javax.annotation.Nonnull;

public abstract class Request<RESULT> extends GoogleHttpClientSpiceRequest<RESULT> {

    private static final String TAG = Request.class.getSimpleName();

    private static final String HEADER_SESSION_ID = "X-Transmission-Session-Id";

    private Server server;
    private String responseSessionId;

    private int statusCode = -1;

    public Request(Class<RESULT> resultClass) {
        super(resultClass);
    }

    public void setServer(@Nonnull Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public int getResponseStatusCode() {
        return statusCode;
    }

    public String getResponseSessionId() {
        return responseSessionId;
    }

    @Override
    public RESULT loadDataFromNetwork() throws Exception {
        if (server == null) {
            throw new IllegalStateException("Server must be set before executing");
        }

        String url = "http://" + server.getHost() + ':' + server.getPort() + "/" + server.getRpcUrl();

        HttpRequestFactory requestFactory = getHttpRequestFactory();
        Log.d(TAG, "requestFactory: " + requestFactory);

        String body = Optional.fromNullable(createBody()).or("");
        HttpContent content = new ByteArrayContent("application/json", body.getBytes());
        Log.d(TAG, "requestBody: " + body);

        HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(url), content);
        request.setThrowExceptionOnExecuteError(false);
        request.setNumberOfRetries(0);

        HttpHeaders headers = new HttpHeaders()
                .setContentType("json")
                .set(HEADER_SESSION_ID, Strings.emptyToNull(server.getLastSessionId()));
        if (server.isAuthenticationEnabled()) {
            headers.setBasicAuthentication(server.getUserName(), server.getPassword());
        }
        request.setHeaders(headers);

        JsonObjectParser jsonParser = new JsonObjectParser.Builder(new JacksonFactory())
                .setWrapperKeys(Collections.singletonList("arguments")).build();
        request.setParser(jsonParser);

        HttpResponse response = request.execute();

        statusCode = response.getStatusCode();
        responseSessionId = response.getHeaders().getFirstHeaderStringValue(HEADER_SESSION_ID);

        RESULT result;
        try {
            //result = response.parseAs(getResultType());
            String responseBody = response.parseAsString();
            Log.d(TAG + "SpiceTransportManager", getClass().getSimpleName() + " responseBody: " + responseBody);
            result = request.getParser().parseAndClose(new StringReader(responseBody), getResultType());
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse response. SC: " + statusCode, e);
            throw e;
        } finally {
            response.disconnect();
        }

        return result;
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

    protected abstract JSONObject getArguments();
}
