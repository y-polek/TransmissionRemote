package net.yupol.transmissionremote.app.utils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.yupol.transmissionremote.app.model.json.Torrent;

public class Filters {
    public static final Predicate<Torrent> ALL = Predicates.alwaysTrue();

    public static final Predicate<Torrent> DOWNLOADING = new Predicate<Torrent>() {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.DOWNLOAD;
        }
    };

    public static final Predicate<Torrent> SEEDING = new Predicate<Torrent>() {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.SEED;
        }
    };

    public static final Predicate<Torrent> ACTIVE = Predicates.or(DOWNLOADING, SEEDING);

    public static final Predicate<Torrent> PAUSED = new Predicate<Torrent>() {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.STOPPED;
        }
    };
}
