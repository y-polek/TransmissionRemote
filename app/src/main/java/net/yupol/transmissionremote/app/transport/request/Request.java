package net.yupol.transmissionremote.app.transport.request;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Strings;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import net.yupol.transmissionremote.app.server.Server;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;

public abstract class Request<RESULT> extends GoogleHttpClientSpiceRequest<RESULT> {

    private static final String TAG = Request.class.getSimpleName();

    private static final JsonObjectParser JSON_PARSER = new JsonObjectParser.Builder(JacksonFactory.getDefaultInstance()).build();

    private Server server;
    private String responseSessionId;

    private int statusCode = -1;

    private String redirectLocation;

    private String responseBody;
    private Throwable error;

    public Request(Class<RESULT> resultClass) {
        super(resultClass);
    }

    public void setServer(@Nonnull Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    @Nullable
    public String getUrl() {
        if (server == null) return null;

        final String address = server.getPort() >= 0 ? server.getHost() + ":" + server.getPort() : server.getHost();
        return String.format(Locale.ROOT, "%s://%s/%s",
                server.useHttps() ? "https" : "http",
                address,
                server.getUrlPath());
    }

    public void setResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public int getResponseStatusCode() {
        return statusCode;
    }

    public String getResponseSessionId() {
        return responseSessionId;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    @Nullable
    public String getResponseBody() {
        return responseBody;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    @Override
    public RESULT loadDataFromNetwork() throws Exception {
        if (server == null) {
            throw new IllegalStateException("Server must be set before executing");
        }

        HttpRequestFactory requestFactory = getHttpRequestFactory();

        String body = Optional.ofNullable(createBody()).orElse("");
        HttpContent content = new ByteArrayContent("application/json", body.getBytes());

        final String url = Optional.ofNullable(getUrl()).orElse("");
        HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(url), content);
        request.setThrowExceptionOnExecuteError(false);
        request.setNumberOfRetries(0);

        HttpHeaders headers = new HttpHeaders()
                .set("x-transmission-session-id", Strings.emptyToNull(server.getLastSessionId()));
        if (server.isAuthenticationEnabled()) {
            headers.setBasicAuthentication(server.getUserName(), server.getPassword());
        }
        request.setHeaders(headers);
        request.setParser(JSON_PARSER);

        HttpResponse response;
        try {
            response = request.execute();
        } catch (Exception e) {
            error = e;
            throw e;
        }

        statusCode = response.getStatusCode();

        try {
            if (HttpStatusCodes.isRedirect(statusCode)) {
                String location = response.getHeaders().getFirstHeaderStringValue("location");
                if (StringUtils.isNotEmpty(location)) {
                    if (location.startsWith("/")) {
                        location = location.substring(1);
                    }
                    if (location.endsWith("/")) {
                        location = location.substring(0, location.length() - 1);
                    }

                    int lastSectionStartIdx = location.lastIndexOf('/');
                    if (lastSectionStartIdx >= 0) {
                        location = location.substring(0, lastSectionStartIdx) + "/rpc";
                    }

                    redirectLocation = location;
                }
                throw new IOException("Request redirected");
            } else {
                responseSessionId = response.getHeaders().getFirstHeaderStringValue("X-Transmission-Session-Id");

                RESULT result;
                try {
                    //result = response.parseAs(getResultType());
                    responseBody = response.parseAsString();

                    JSONObject responseBodyJson = new JSONObject(responseBody);

                    String resultStatus = responseBodyJson.getString("result");
                    if (!"success".equalsIgnoreCase(resultStatus)) {
                        throw new ResponseFailureException(resultStatus);
                    }

                    result = request.getParser().parseAndClose(
                            new StringReader(responseBodyJson.getString("arguments")),
                            getResultType());
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse response. SC: " + statusCode, e);
                    throw e;
                }
                return result;
            }
        } finally {
            response.disconnect();
        }
    }

    public String createBody() {
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
