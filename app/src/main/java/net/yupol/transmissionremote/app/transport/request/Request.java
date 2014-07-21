package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.transport.response.Response;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

public interface Request {

    public JSONObject getBody();

    public Header[] getHeaders();

    public void setSessionId(String sessionId);

    public Response responseWrapper(HttpResponse httpResponse);
}
