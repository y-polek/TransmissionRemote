package net.yupol.transmissionremote.app.filtering;

import android.support.annotation.NonNull;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.model.json.Torrent;

public class NameFilter extends BaseFilter {

    private String query;

    public NameFilter() {
        super(R.string.filter_name, R.string.filter_empty_name);
    }

    public NameFilter withQuery(@NonNull String query) {
        this.query = query.toLowerCase();
        return this;
    }

    @Override
    public boolean apply(Torrent torrent) {
        return torrent.getName().toLowerCase().contains(query);
    }
}
