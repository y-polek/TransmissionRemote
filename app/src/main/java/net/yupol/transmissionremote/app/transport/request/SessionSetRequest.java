package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.transport.response.Response;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

public class SessionSetRequest extends BaseRequest {

    private JSONObject arguments;

    public SessionSetRequest(JSONObject arguments) {
        super("session-set");
        this.arguments = arguments;
    }

    @Override
    protected JSONObject getArguments() {
        return arguments;
    }
}
