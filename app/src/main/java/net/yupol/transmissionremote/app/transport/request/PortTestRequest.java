package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.model.json.PortTestResult;

public class PortTestRequest extends Request<PortTestResult> {

    public PortTestRequest() {
        super(PortTestResult.class);
    }

    @Override
    public String getMethod() {
        return "port-test";
    }

    @Override
    public String getArguments() {
        return null;
    }
}
