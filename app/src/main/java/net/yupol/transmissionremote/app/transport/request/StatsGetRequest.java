package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.ServerStats;

import org.json.JSONObject;

public class StatsGetRequest extends Request<ServerStats>{
    public StatsGetRequest() { super(ServerStats.class); }

    @Override
    protected String getMethod() {
        return "session-stats";
    }

    @Override
    protected JSONObject getArguments() {
        return null;
    }
}
