package net.yupol.transmissionremote.app.transport.requests;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

public interface Request {

    public JSONObject getBody();

    public Header[] getHeaders();

    public void setSessionId(String sessionId);

}
