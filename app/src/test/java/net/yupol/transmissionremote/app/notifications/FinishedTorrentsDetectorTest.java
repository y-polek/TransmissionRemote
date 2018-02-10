package net.yupol.transmissionremote.app.notifications;

import com.google.common.collect.ImmutableSet;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.server.Server;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.emptyCollectionOf;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

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

        assertThat(detector.findLastFinishedDate(torrents), equalTo(873L));
    }

    @Test
    public void testFindLastFinishedDateWithEmptyList() {
        assertThat(detector.findLastFinishedDate(Collections.<Torrent>emptyList()), equalTo(-1L));
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

        assertThat(filteredTorrents, emptyCollectionOf(Torrent.class));
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

        Collection<Torrent> filteredTorrents = detector.filterFinishedTorrentsToNotify(torrents, server);

        assertThat(filteredTorrents, hasSize(2));
        assertThat(filteredTorrents, hasItem(withDoneDate(555L)));
        assertThat(filteredTorrents, hasItem(withDoneDate(873L)));
    }

    private static Matcher<Torrent> withDoneDate(final long date) {
        return new BaseMatcher<Torrent>() {
            @Override
            public boolean matches(Object item) {
                return ((Torrent) item).getDoneDate() == date;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("getDoneDate() should return ").appendValue(date);
            }
        };
    }
}
