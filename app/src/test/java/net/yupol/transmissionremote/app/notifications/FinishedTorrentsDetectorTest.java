package net.yupol.transmissionremote.app.notifications;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableSet;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.server.Server;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

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
        assertThat(detector.findLastFinishedDate(Collections.emptyList())).isEqualTo(-1L);
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
        Collection<Torrent> torrents = ImmutableSet.of(
                new Torrent.Builder().doneDate(-1L).build(),
                new Torrent.Builder().doneDate(100L).build(),
                new Torrent.Builder().doneDate(555L).build(),
                new Torrent.Builder().doneDate(873L).build(),
                new Torrent.Builder().doneDate(0L).build()
        );

        Torrent[] filteredTorrents = detector.filterFinishedTorrentsToNotify(torrents, server).toArray(new Torrent[0]);

        assertThat(filteredTorrents).hasLength(2);
        assertThat(filteredTorrents[0].getDoneDate()).isEqualTo(555L);
        assertThat(filteredTorrents[1].getDoneDate()).isEqualTo(873L);
    }
}
