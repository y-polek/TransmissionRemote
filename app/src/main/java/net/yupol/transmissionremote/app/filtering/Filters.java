package net.yupol.transmissionremote.app.filtering;

import net.yupol.transmissionremote.app.model.json.Torrent;

import static net.yupol.transmissionremote.app.R.string.filter_active;
import static net.yupol.transmissionremote.app.R.string.filter_all;
import static net.yupol.transmissionremote.app.R.string.filter_downloading;
import static net.yupol.transmissionremote.app.R.string.filter_empty_active;
import static net.yupol.transmissionremote.app.R.string.filter_empty_all;
import static net.yupol.transmissionremote.app.R.string.filter_empty_downloading;
import static net.yupol.transmissionremote.app.R.string.filter_empty_finished;
import static net.yupol.transmissionremote.app.R.string.filter_empty_paused;
import static net.yupol.transmissionremote.app.R.string.filter_empty_seeding;
import static net.yupol.transmissionremote.app.R.string.filter_finished;
import static net.yupol.transmissionremote.app.R.string.filter_paused;
import static net.yupol.transmissionremote.app.R.string.filter_seeding;

public class Filters {
    public static final Filter ALL = new BaseFilter(filter_all, filter_empty_all) {
        @Override
        public boolean apply(Torrent torrent) {
            return true;
        }
    };

    public static final Filter DOWNLOADING = new BaseFilter(filter_downloading, filter_empty_downloading) {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.DOWNLOAD;
        }
    };

    public static final Filter SEEDING = new BaseFilter(filter_seeding, filter_empty_seeding) {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.SEED;
        }
    };

    public static final Filter ACTIVE = new BaseFilter(filter_active, filter_empty_active) {
        @Override
        public boolean apply(Torrent torrent) {
            return DOWNLOADING.apply(torrent) || SEEDING.apply(torrent);
        }
    };

    public static final Filter PAUSED = new BaseFilter(filter_paused, filter_empty_paused) {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.getStatus() == Torrent.Status.STOPPED;
        }
    };

    public static final Filter FINISHED = new BaseFilter(filter_finished, filter_empty_finished) {
        @Override
        public boolean apply(Torrent torrent) {
            return torrent.isFinished();
        }
    };
}
