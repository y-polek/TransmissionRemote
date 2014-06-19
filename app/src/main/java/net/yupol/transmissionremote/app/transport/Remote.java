package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import net.yupol.transmissionremote.app.transport.requests.CheckPortRequest;
import net.yupol.transmissionremote.app.transport.requests.Request;
import net.yupol.transmissionremote.app.transport.requests.UpdateTorrentsRequest;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

public class Remote {

    private static final String TAG = Remote.class.getName();

    private static final String URL_ENDING = "transmission/rpc";

    private static final String[] TORRENT_METADATA = {
            Torrent.Metadata.ID,
            Torrent.Metadata.NAME,
            Torrent.Metadata.TOTAL_SIZE,
            Torrent.Metadata.ADDED_DATE,
            Torrent.Metadata.STATUS
    };

    private URI uri;
    private String sessionId = "";

    public Remote(String rootUrl) {
        String urlStr = new StringBuilder(rootUrl).append('/').append(URL_ENDING).toString();
        try {
            uri = new URI(urlStr);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Syntax error in URI '" + uri + "'", e);
        }
    }

    public Response sendRequest(Request request, boolean resendIfWrongId) throws IOException {

        request.setSessionId(sessionId);

        HttpPost post = new HttpPost(uri);
        for (Header header : request.getHeaders())
            post.addHeader(header);

        try {
            post.setEntity(new ByteArrayEntity(request.getBody().toString().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Unsupported encoding exception", e);
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(post);
        Response response = new Response(httpResponse);

        Log.d(TAG, "Response: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.SC_CONFLICT && resendIfWrongId) {
            sessionId = response.getSessionId();
            sendRequest(request, false);
        }

        return response;
    }

    public boolean checkPort() throws IOException {
        Request request = new CheckPortRequest();

        Response response = sendRequest(request, true);

        if (response.getStatusCode() != HttpStatus.SC_OK)
            return false;

        String body = response.getBody();

        try {
            JSONObject jsonBody = new JSONObject(body);
            JSONObject argumentsObj = jsonBody.getJSONObject("arguments");
            return argumentsObj.getBoolean("port-is-open");
        } catch (JSONException e) {
            Log.e(TAG, "Can't parse checkPort response", e);
            return false;
        }
    }

    public void updateTorrents() throws IOException {
        Request request = new UpdateTorrentsRequest();

        Response response = sendRequest(request, true);





    }
}
