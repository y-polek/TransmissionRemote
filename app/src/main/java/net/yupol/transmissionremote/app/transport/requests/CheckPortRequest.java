package net.yupol.transmissionremote.app.transport.requests;

import org.json.JSONObject;

public class CheckPortRequest extends BaseRequest {

    public CheckPortRequest() {
        super("port-test");
    }

    @Override
    protected JSONObject getArguments() {
        return null;
    }
}
