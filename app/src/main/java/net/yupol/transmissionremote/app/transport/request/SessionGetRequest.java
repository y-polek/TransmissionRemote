package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.model.json.ServerSettings;

import org.json.JSONObject;

public class SessionGetRequest extends Request<ServerSettings> {

    public SessionGetRequest() {
        super(ServerSettings.class);
    }

    @Override
    protected String getMethod() {
        return "session-get";
    }

    @Override
    protected JSONObject getArguments() {
        return null;
    }
}
