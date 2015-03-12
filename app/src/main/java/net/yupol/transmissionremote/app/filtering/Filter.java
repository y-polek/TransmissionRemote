package net.yupol.transmissionremote.app.filtering;

import com.google.common.base.Predicate;

import net.yupol.transmissionremote.app.TorrentListFragment;
import net.yupol.transmissionremote.app.model.json.Torrent;

public interface Filter extends Predicate<Torrent> {

    public int getEmptyMessageRes();
}
