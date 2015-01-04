package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

public class PortTestResult {

    @Key("port-is-open")
    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public String toString() {
        return "PortTestResult{" +
                "isOpen=" + isOpen +
                '}';
    }
}
