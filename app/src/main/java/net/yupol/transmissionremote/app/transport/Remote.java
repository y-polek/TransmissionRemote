package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.CheckPortRequest;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.request.UpdateTorrentsRequest;
import net.yupol.transmissionremote.app.transport.response.Response;

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

    private static final String TAG = Remote.class.getSimpleName();

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

    public Remote(Server server) {

        String urlStr = new StringBuilder("http://")
                .append(server.getHost())
                .append(':').append(server.getPort())
                .append('/').append(URL_ENDING).toString();
        try {
            uri = new URI(urlStr);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Syntax error in URI '" + uri + "'", e);
        }
    }

    public Response sendRequest(Request request, boolean resendIfWrongId) throws IOException {

        Log.d(TAG, "Remote.sendRequest() resend: " + resendIfWrongId + " body: " + request.getBody());

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
        Response response = request.responseWrapper(httpResponse);

        if (response.getStatusCode() == HttpStatus.SC_CONFLICT && resendIfWrongId) {
            Log.d(TAG, "SC_CONFLICT resend: " + resendIfWrongId + " response: " + response.getBody());
            Log.d(TAG, "Old session ID: " + sessionId);
            sessionId = response.getSessionId();
            Log.d(TAG, "New session ID: " + sessionId);
            response = sendRequest(request, false);
        }

        return response;
    }
}
