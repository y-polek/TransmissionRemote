package net.yupol.transmissionremote.app.server;

import net.yupol.transmissionremote.model.Server;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ServerTest {

    @Test
    public void testIllegalPort() {
        try {
            new Server("name", "192.168.1.1", 0xFFFF + 1);
            fail("Port must be <= " + 0xFFFF);
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new Server("name", "192.168.1.1", 1);
            new Server("name", "192.168.1.1", 9091);
            new Server("name", "192.168.1.1", 0xFFFF);
        } catch (Throwable e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testJsonSerialization() {
        Server server = new Server("name", "192.168.1.1", 9091);
        assertEquals(server, Server.fromJson(server.toJson()));
    }

    @Test
    public void testJsonSerializationWithLastUpdatedField() {
        Server server = new Server("FeeNAS", "localhost", 9092);
        final long lastUpdateDate = 12345L;
        server.setLastUpdateDate(lastUpdateDate);
        Server deserializedServer = Server.fromJson(server.toJson());

        assertThat(deserializedServer.getLastUpdateDate(), equalTo(lastUpdateDate));
    }

    @Test
    public void testEquals() {
        Server s1 = new Server("name", "192.168.1.1", 9091);
        Server s2 = new Server("name", "192.168.1.1", 9091);
        assertFalse(s1.equals(s2));

        Server deserializedS1 = Server.fromJson(s1.toJson());
        assertEquals(s1, deserializedS1);

        assertEquals(s1, deserializedS1);
    }

    @Test
    public void testSavedDownloadLocations() {
        Server s1 = new Server("name", "192.168.1.1", 9091);

        s1.addSavedDownloadLocations("/mnt/DOWNLOADS");
        assertEquals(1, s1.getSavedDownloadLocations().size());

        s1.addSavedDownloadLocations("/mnt/Downloads");
        assertEquals(1, s1.getSavedDownloadLocations().size());

        s1.addSavedDownloadLocations("/home/Documents");
        assertEquals(2, s1.getSavedDownloadLocations().size());

        String savedStr = s1.toJson();
        Server restoredServer = Server.fromJson(savedStr);
        assertEquals(s1.getSavedDownloadLocations().size(), restoredServer.getSavedDownloadLocations().size());
    }

    @Test
    public void testMaxSavedDownloadLocations() {
        Server s1 = new Server("name", "192.168.1.1", 9091);
        s1.addSavedDownloadLocations("/home/Documents1");
        s1.addSavedDownloadLocations("/home/Documents2");
        s1.addSavedDownloadLocations("/home/Documents3");
        s1.addSavedDownloadLocations("/home/Documents4");
        s1.addSavedDownloadLocations("/home/Documents5");
        s1.addSavedDownloadLocations("/home/Documents6");
        assertEquals(Server.MAX_SAVED_DOWNLOAD_LOCATIONS, s1.getSavedDownloadLocations().size());
    }
}
