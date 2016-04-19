package net.yupol.transmissionremote.app.server;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import java.util.UUID;

import javax.annotation.Nonnull;

public class Server implements Parcelable {

    public static final String TAG = Server.class.getSimpleName();

    private UUID id;
    private String idStr;
    private String name;
    private String host;
    private int port;
    private boolean useAuthentication;
    private String userName;
    private String password;
    private String lastSessionId;

    public Server(@Nonnull String name, @Nonnull String host, int port) {
        if (port <= 0 || port > 0xFFFF)
            throw new IllegalArgumentException("Port number value must be in range [1, 65535], actual value: " + port);
        setId(UUID.randomUUID());
        this.name = name;
        this.host = host;
        this.port = port;

        useAuthentication = false;
    }

    public Server(@Nonnull String name, @Nonnull String host, int port,
                  @Nonnull String userName, @Nonnull String password) {
        this(name, host, port);

        useAuthentication = true;
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(@Nonnull String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAuthenticationEnabled() {
        return useAuthentication;
    }

    public void setAuthenticationEnabled(boolean isEnabled) {
        useAuthentication = isEnabled;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastSessionId(String sessionId) {
        lastSessionId = sessionId;
    }

    public String getLastSessionId() {
        return lastSessionId;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    private void setId(UUID id) {
        this.id = id;
        this.idStr = id.toString();
    }

    public String getId() {
        return idStr;
    }

    public static Server fromJson(String jsonObj) {
        return new Gson().fromJson(jsonObj, Server.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (port != server.port) return false;
        if (useAuthentication != server.useAuthentication) return false;
        if (!host.equals(server.host)) return false;
        if (!id.equals(server.id)) return false;
        if (!name.equals(server.name)) return false;
        if (userName != null ? !userName.equals(server.userName) : server.userName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (useAuthentication ? 1 : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", useAuthentication=" + useAuthentication +
                ", userName='" + userName + '\'' +
                ", lastSessionId='" + lastSessionId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        dest.writeString(name);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeByte((byte) (useAuthentication ? 1 : 0));
        if (useAuthentication) {
            dest.writeString(userName);
            dest.writeString(password);
        }
        dest.writeString(Strings.nullToEmpty(lastSessionId));
    }

    public static final Creator<Server> CREATOR = new Creator<Server>() {

        @Override
        public Server createFromParcel(Parcel parcel) {
            UUID id = (UUID) parcel.readSerializable();
            String name = parcel.readString();
            String host = parcel.readString();
            int port = parcel.readInt();
            Server server;
            boolean useAuthentication = parcel.readByte() != 0;
            if (useAuthentication) {
                String userName = parcel.readString();
                String password = parcel.readString();
                server = new Server(name, host, port, userName, password);
            } else {
                server = new Server(name, host, port);
            }
            server.setId(id);
            String sessionId = Strings.emptyToNull(parcel.readString());
            server.setLastSessionId(sessionId);
            return server;
        }

        @Override
        public Server[] newArray(int i) {
            return new Server[i];
        }
    };
}
