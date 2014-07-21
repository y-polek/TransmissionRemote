package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.transport.response.CheckPortResponse;
import net.yupol.transmissionremote.app.transport.response.Response;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

public class CheckPortRequest extends BaseRequest {

    public CheckPortRequest() {
        super("port-test");
    }

    @Override
    protected JSONObject getArguments() {
        return null;
    }

    @Override
    public Response responseWrapper(HttpResponse httpResponse) {
        return new CheckPortResponse(httpResponse);
    }
}
