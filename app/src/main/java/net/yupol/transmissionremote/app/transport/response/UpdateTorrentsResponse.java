package net.yupol.transmissionremote.app.transport.response;

import net.yupol.transmissionremote.app.transport.Torrent;

import org.apache.http.HttpResponse;
import org.json.JSONArray;

import java.util.Collections;
import java.util.List;

public class UpdateTorrentsResponse extends Response {

    private List<Torrent> torrents = Collections.emptyList();

    public UpdateTorrentsResponse(HttpResponse response) {
        super(response);

        JSONArray array = getArguments().optJSONArray("torrents");
        if (array != null) {
            torrents = Torrent.fromArray(array);
        }
    }

    public List<Torrent> getTorrents() {
        return torrents;
    }
}
