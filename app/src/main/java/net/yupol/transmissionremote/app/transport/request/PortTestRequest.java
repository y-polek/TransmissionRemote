package net.yupol.transmissionremote.app.transport.request;

public class PortTestRequest extends Request<Boolean> {

    public PortTestRequest() {
        super(Boolean.class);
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
