package net.yupol.transmissionremote.app.transport.request;

import org.json.JSONObject;

public class SessionSetRequest extends Request<Void> {

    private JSONObject arguments;

    public SessionSetRequest(JSONObject arguments) {
        super(Void.class);
        this.arguments = arguments;
    }

    @Override
    protected String getMethod() {
        return "session-set";
    }

    @Override
    protected String getArguments() {
        return arguments.toString();
    }
}
