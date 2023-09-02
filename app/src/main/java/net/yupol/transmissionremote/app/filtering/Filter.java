package net.yupol.transmissionremote.app.filtering;

import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.function.Predicate;

public interface Filter extends Predicate<Torrent> {

    int getNameResId();

    int getEmptyMessageResId();
}
