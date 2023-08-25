package net.yupol.transmissionremote.app.server;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ServerTest {

    @Test
    public void testIllegalPort() {
        new Server("name", "192.168.1.1", 1);
        new Server("name", "192.168.1.1", 9091);
        new Server("name", "192.168.1.1", 0xFFFF);

        try {
            new Server("name", "192.168.1.1", 0xFFFF + 1);
            fail("Port must be <= " + 0xFFFF);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo(
                    "Port number value must be in range [1, 65535], actual value: " + 65536
            );
        }
    }

    @Test
    public void testJsonSerialization() {
        Server server = new Server("name", "192.168.1.1", 9091);
        assertThat(Server.fromJson(server.toJson())).isEqualTo(server);

        server.setLastSessionId("a;ldskfja;lsdfkj");
        assertThat(Server.fromJson(server.toJson())).isEqualTo(server);
    }

    @Test
    public void testJsonSerializationWithLastUpdatedField() {
        Server server = new Server("FeeNAS", "localhost", 9092);
        final long lastUpdateDate = 12345L;
        server.setLastUpdateDate(lastUpdateDate);
        Server deserializedServer = Server.fromJson(server.toJson());

        assertThat(deserializedServer.getLastUpdateDate()).isEqualTo(lastUpdateDate);
    }

    @Test
    public void testEquals() {
        Server s1 = new Server("name", "192.168.1.1", 9091);
        Server s2 = new Server("name", "192.168.1.1", 9091);
        assertThat(s1).isNotEqualTo(s2);

        s1.setLastSessionId("slfajsldfkajsfa;fa");
        Server deserializedS1 = Server.fromJson(s1.toJson());
        assertThat(deserializedS1).isEqualTo(s1);

        deserializedS1.setLastSessionId(null);
        assertThat(deserializedS1).isEqualTo(s1);
    }

    @Test
    public void testSavedDownloadLocations() {
        Server s1 = new Server("name", "192.168.1.1", 9091);

        s1.addSavedDownloadLocations("/mnt/DOWNLOADS");
        assertThat(s1.getSavedDownloadLocations()).hasSize(1);

        s1.addSavedDownloadLocations("/mnt/Downloads");
        assertThat(s1.getSavedDownloadLocations()).hasSize(1);

        s1.addSavedDownloadLocations("/home/Documents");
        assertThat(s1.getSavedDownloadLocations()).hasSize(2);

        String savedStr = s1.toJson();
        Server restoredServer = Server.fromJson(savedStr);
        assertThat(restoredServer.getSavedDownloadLocations()).hasSize(s1.getSavedDownloadLocations().size());
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
        assertThat(s1.getSavedDownloadLocations()).hasSize(Server.MAX_SAVED_DOWNLOAD_LOCATIONS);
    }
}
