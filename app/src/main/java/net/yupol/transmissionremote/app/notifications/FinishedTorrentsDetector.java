package net.yupol.transmissionremote.app.notifications;

import com.google.common.base.Predicate;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.server.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import static com.google.common.collect.FluentIterable.from;

public class FinishedTorrentsDetector {

    public Collection<Torrent> filterFinishedTorrentsToNotify(Collection<Torrent> torrents, Server server) {
        long lastUpdateDate = server.getLastUpdateDate();
        if (lastUpdateDate <= 0) return Collections.emptySet();

        return filterFinishedAfterDate(torrents, lastUpdateDate);
    }

    /**
     * @return date of last finished torrent or {@code -1} if list is empty
     */
    public long findLastFinishedDate(Collection<Torrent> torrents) {
        if (torrents.isEmpty()) return -1;

        List<Torrent> sortedTorrents = sortByDoneDate(torrents);
        return sortedTorrents.get(0).getDoneDate();
    }

    private Collection<Torrent> filterFinishedAfterDate(Collection<Torrent> torrents, final long date) {
        return from(torrents).filter(new Predicate<Torrent>() {
            @Override
            public boolean apply(@Nonnull Torrent torrent) {
                return torrent.getDoneDate() > date;
            }
        }).toSet();
    }

    private List<Torrent> sortByDoneDate(Collection<Torrent> torrents) {
        return from(torrents).toSortedList(Collections.reverseOrder(new Comparator<Torrent>() {
            @Override
            public int compare(Torrent t1, Torrent t2) {
                return compare(t1.getDoneDate(), t2.getDoneDate());
            }

            private int compare(long x, long y) {
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        }));
    }
}
