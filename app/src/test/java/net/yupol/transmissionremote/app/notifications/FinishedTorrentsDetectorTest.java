package net.yupol.transmissionremote.app.notifications;

import com.google.common.collect.ImmutableSet;

import net.yupol.transmissionremote.model.Server;
import net.yupol.transmissionremote.model.json.Torrent;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class FinishedTorrentsDetectorTest {

    private FinishedTorrentsDetector detector;

    @Before
    public void setup() {
        detector = new FinishedTorrentsDetector();
    }

    @Test
    public void testFindLastFinishedDate() {
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                new Torrent.Builder().doneDate(555L).build(),
                new Torrent.Builder().doneDate(873L).build(),
                new Torrent.Builder().doneDate(0L).build()
        );

        assertThat(detector.findLastFinishedDate(torrents)).isEqualTo(873L);
    }

    @Test
    public void testFindLastFinishedDateWithEmptyList() {
        assertThat(detector.findLastFinishedDate(Collections.<Torrent>emptyList())).isEqualTo(-1L);
    }

    @Test
    public void testFilterFinishedTorrentsToNotifyFirstUpdate() {
        Server server = new Server("test", "localhost", 9091);
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                new Torrent.Builder().doneDate(555L).build(),
                new Torrent.Builder().doneDate(873L).build(),
                new Torrent.Builder().doneDate(0L).build()
        );

        Collection<Torrent> filteredTorrents = detector.filterFinishedTorrentsToNotify(torrents, server);

        assertThat(filteredTorrents).isEmpty();
    }

    @Test
    public void testFilterFinishedTorrentsToNotify() {
        Server server = new Server("test", "localhost", 9091);
        server.setLastUpdateDate(100L);
        Torrent torrent555 = new Torrent.Builder().doneDate(555L).build();
        Torrent torrent873 = new Torrent.Builder().doneDate(873L).build();
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                torrent555,
                torrent873,
                new Torrent.Builder().doneDate(0L).build()
        );

        Collection<Torrent> filteredTorrents = detector.filterFinishedTorrentsToNotify(torrents, server);

        assertThat(filteredTorrents).hasSize(2);
        assertThat(filteredTorrents).containsOnly(torrent555, torrent873);
    }
}
