package net.yupol.transmissionremote.app.server;

import android.os.Parcel;

import junit.framework.TestCase;

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
        assertEquals(server, Server.fromJson(server.toJson()));

        server.setLastSessionId("a;ldskfja;lsdfkj");
        assertEquals(server, Server.fromJson(server.toJson()));
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
        Server deserializedS1 = Server.fromJson(s1.toJson());
        assertEquals(s1, deserializedS1);

        deserializedS1.setLastSessionId(null);
        assertEquals(s1, deserializedS1);
    }
}
