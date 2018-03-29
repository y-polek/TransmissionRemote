package net.yupol.transmissionremote.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Server implements Parcelable {

    public static final String TAG = Server.class.getSimpleName();
    public static final String DEFAULT_RPC_URL = "transmission/rpc";

    static int MAX_SAVED_DOWNLOAD_LOCATIONS = 5;

    private UUID id;
    private String idStr;
    private String name;
    private String host;
    private int port;
    private boolean useAuthentication;
    private String userName;
    private String password;
    private String rpcUrl = DEFAULT_RPC_URL;
    private String lastSessionId;
    private String redirectLocation;
    private boolean useHttps;
    private boolean trustSelfSignedSslCert;
    private List<String> savedDownloadLocations = new LinkedList<>();
    private long lastUpdateDate;

    public Server(@NonNull String name, @NonNull String host, int port) {
        if (port <= 0 || port > 0xFFFF)
            throw new IllegalArgumentException("Port number value must be in range [1, 65535], actual value: " + port);
        setId(UUID.randomUUID());
        this.name = name;
        this.host = host;
        this.port = port;

        useAuthentication = false;
    }

    public Server(@NonNull String name, @NonNull String host, int port,
                  @NonNull String userName, @NonNull String password) {
        this(name, host, port);

        useAuthentication = true;
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(@NonNull String host) {
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

    @NonNull
    public String getRpcUrl() {
        return rpcUrl;
    }

    public String getUrlPath() {
        return redirectLocation == null ? rpcUrl : redirectLocation;
    }

    public void setRpcUrl(@NonNull String url) {
        this.rpcUrl = url;
    }

    public void setLastSessionId(String sessionId) {
        lastSessionId = sessionId;
    }

    public String getLastSessionId() {
        return lastSessionId;
    }

    public void setRedirectLocation(String location) {
        redirectLocation = location;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }

    public boolean useHttps() {
        return useHttps;
    }

    public void setTrustSelfSignedSslCert(boolean trust) {
        trustSelfSignedSslCert = trust;
    }

    public boolean getTrustSelfSignedSslCert() {
        return trustSelfSignedSslCert;
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

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public static Server fromJson(String jsonObj) {
        return new Gson().fromJson(jsonObj, Server.class);
    }

    public List<String> getSavedDownloadLocations() {
        return savedDownloadLocations != null ? savedDownloadLocations : Collections.<String>emptyList();
    }

    public void addSavedDownloadLocations(String location) {
        if (savedDownloadLocations == null) {
            savedDownloadLocations = new LinkedList<>();
        }
        for (String l : savedDownloadLocations) {
            if (l.equalsIgnoreCase(location)) return;
        }
        savedDownloadLocations.add(0, location);
        if (savedDownloadLocations.size() > MAX_SAVED_DOWNLOAD_LOCATIONS) {
            savedDownloadLocations.remove(savedDownloadLocations.size() - 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (port != server.port) return false;
        if (useAuthentication != server.useAuthentication) return false;
        if (useHttps != server.useHttps) return false;
        if (trustSelfSignedSslCert != server.trustSelfSignedSslCert) return false;
        if (!id.equals(server.id)) return false;
        if (!name.equals(server.name)) return false;
        if (!host.equals(server.host)) return false;
        if (userName != null ? !userName.equals(server.userName) : server.userName != null)
            return false;
        return rpcUrl != null ? rpcUrl.equals(server.rpcUrl) : server.rpcUrl == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (useAuthentication ? 1 : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (rpcUrl != null ? rpcUrl.hashCode() : 0);
        result = 31 * result + (useHttps ? 1 : 0);
        result = 31 * result + (trustSelfSignedSslCert ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", userHttps=" + useHttps +
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
        dest.writeString(rpcUrl);
        dest.writeString(Strings.nullToEmpty(lastSessionId));
        dest.writeByte((byte) (useHttps ? 1 : 0));
        dest.writeByte((byte) (trustSelfSignedSslCert ? 1 : 0));
        dest.writeStringList(savedDownloadLocations);
        dest.writeLong(lastUpdateDate);
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
            server.setRpcUrl(parcel.readString());
            String sessionId = Strings.emptyToNull(parcel.readString());
            server.setLastSessionId(sessionId);
            server.useHttps = parcel.readByte() != 0;
            server.trustSelfSignedSslCert = parcel.readByte() != 0;
            parcel.readStringList(server.savedDownloadLocations);
            server.lastUpdateDate = parcel.readLong();
            return server;
        }

        @Override
        public Server[] newArray(int i) {
            return new Server[i];
        }
    };
}
