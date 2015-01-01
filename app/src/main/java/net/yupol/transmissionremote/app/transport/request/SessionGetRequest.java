package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.model.json.ServerSettings;

public class SessionGetRequest extends Request<ServerSettings> {

    public SessionGetRequest() {
        super(ServerSettings.class);
    }

    @Override
    protected String getMethod() {
        return "session-get";
    }

    @Override
    protected String getArguments() {
        return null;
    }
}
