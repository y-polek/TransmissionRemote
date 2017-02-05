package net.yupol.transmissionremote.app.server;

import android.os.Parcel;

import junit.framework.TestCase;

import static net.yupol.transmissionremote.app.server.Server.fromJson;

public class ServerTest extends TestCase {

    public void testIllegalPort() {
        try {
            new Server("name", "192.168.1.1", 0);
            fail("Port must be > " + 0);
        } catch (IllegalArgumentException e) {
            // ok
        }

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

    public void testJsonSerialization() {
        Server server = new Server("name", "192.168.1.1", 9091);
        assertEquals(server, fromJson(server.toJson()));

        server.setLastSessionId("a;ldskfja;lsdfkj");
        assertEquals(server, fromJson(server.toJson()));
    }

    public void testParcelSerialization() {
        Server server = new Server("name", "192.168.1.1", 9091);
        Parcel parcel = Parcel.obtain();
        server.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        assertEquals(server, Server.CREATOR.createFromParcel(parcel));

        server.setLastSessionId("a;ldskfja;lsdfkj");
        parcel = Parcel.obtain();
        server.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        assertEquals(server, Server.CREATOR.createFromParcel(parcel));
    }

    public void testEquals() {
        Server s1 = new Server("name", "192.168.1.1", 9091);
        Server s2 = new Server("name", "192.168.1.1", 9091);
        assertFalse(s1.equals(s2));

        s1.setLastSessionId("slfajsldfkajsfa;fa");
        Server deserializedS1 = fromJson(s1.toJson());
        assertEquals(s1, deserializedS1);

        deserializedS1.setLastSessionId(null);
        assertEquals(s1, deserializedS1);
    }

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
