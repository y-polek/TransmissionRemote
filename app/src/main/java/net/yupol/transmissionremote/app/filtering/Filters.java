package net.yupol.transmissionremote.app.filtering;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.Torrent;

import javax.annotation.Nullable;

public class Filters {
    public static final Filter ALL = new Filter() {
        @Override
        public boolean apply(Torrent torrent) {
            return true;
        }

        @Override
        public int getEmptyMessageRes() {
            return R.string.filter_empty_all;
        }
    };

    public static final Filter DOWNLOADING = new Filter() {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.DOWNLOAD;
        }

        @Override
        public int getEmptyMessageRes() {
            return R.string.filter_empty_downloading;
        }
    };

    public static final Filter SEEDING = new Filter() {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.SEED;
        }

        @Override
        public int getEmptyMessageRes() {
            return R.string.filter_empty_seeding;
        }
    };

    public static final Filter ACTIVE = new Filter() {
        @Override
        public boolean apply(Torrent torrent) {
            return DOWNLOADING.apply(torrent) || SEEDING.apply(torrent);
        }

        @Override
        public int getEmptyMessageRes() {
            return R.string.filter_empty_active;
        }
    };

    public static final Filter PAUSED = new Filter() {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.STOPPED;
        }

        @Override
        public int getEmptyMessageRes() {
            return R.string.filter_empty_paused;
        }
    };
}
