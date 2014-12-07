package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.transport.response.Response;
import net.yupol.transmissionremote.app.transport.response.SessionGetResponse;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

public class SessionGetRequest extends BaseRequest {

    public SessionGetRequest() {
        super("session-get");
    }

    @Override
    protected JSONObject getArguments() {
        return new JSONObject();
    }

    @Override
    public Response responseWrapper(HttpResponse httpResponse) {
        return new SessionGetResponse(httpResponse);
    }
}
