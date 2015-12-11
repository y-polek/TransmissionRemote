package net.yupol.transmissionremote.app.sorting;

import com.google.common.base.Optional;

import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.Comparator;

public class TorrentComparators {

    public static final Comparator<Torrent> NAME = new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            String name1 = Optional.fromNullable(t1.getName()).or("");
            String name2 = Optional.fromNullable(t2.getName()).or("");
            return name1.compareToIgnoreCase(name2);
        }
    };

    public static final Comparator<Torrent> SIZE = new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            return Long.signum(t1.getTotalSize() - t2.getTotalSize());
        }
    };

    public static final Comparator<Torrent> TIME_REMAINING = new Comparator<Torrent>() {
        @Override
        public int compare(Torrent t1, Torrent t2) {
            long l1 = t1.getLeftUntilDone();
            long l2 = t2.getLeftUntilDone();
            return l1 < l2 ? -1 : (l1 == l2 ? 0 : 1);
        }
    };
}
