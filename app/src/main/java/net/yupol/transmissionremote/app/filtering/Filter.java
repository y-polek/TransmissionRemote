package net.yupol.transmissionremote.app.filtering;

import com.google.common.base.Predicate;

import net.yupol.transmissionremote.model.json.Torrent;

public interface Filter extends Predicate<Torrent> {

    int getNameResId();

    int getEmptyMessageResId();
}
