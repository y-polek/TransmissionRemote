package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

public class ServerStats {
    @Key("cumulative-stats") public TransferStats cumulativeStats;
    @Key("current-stats") public TransferStats currentStats;
}
