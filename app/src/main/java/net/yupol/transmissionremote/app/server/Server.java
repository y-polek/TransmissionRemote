package net.yupol.transmissionremote.app.server;

import android.os.Parcel;
import android.os.Parcelable;

import static com.google.common.base.Preconditions.*;

public class Server implements Parcelable {

    private String name;
    private String host;
    private int port;
    private boolean useAuthentication;
    private String userName;
    private String password;

    public Server(String name, String host, int port) {
        checkNotNull(name, "Name must be non null");
        checkNotNull(host, "Host name must be non null");
        if (port <=0 || port > 0xFFFF)
            throw new IllegalArgumentException("Port number value must be in range [1, 65535], actual value: " + port);
        this.name = name;
        this.host = host;
        this.port = port;

        useAuthentication = false;
    }

    public Server(String name, String host, int port, String userName, String password) {
        this(name, host, port);

        useAuthentication = true;
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", useAuthentication=" + useAuthentication +
                ", userName='" + userName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeByte((byte) (useAuthentication ? 1 : 0));
        if (useAuthentication) {
            dest.writeString(userName);
            dest.writeString(password);
        }
    }

    public static final Creator<Server> CREATOR = new Creator<Server>() {

        @Override
        public Server createFromParcel(Parcel parcel) {
            String name = parcel.readString();
            String host = parcel.readString();
            int port = parcel.readInt();
            boolean useAuthentication = parcel.readByte() == 0 ? false : true;
            if (useAuthentication) {
                String userName = parcel.readString();
                String password = parcel.readString();
                return new Server(name, host, port, userName, password);
            } else {
                return new Server(name, host, port);
            }
        }

        @Override
        public Server[] newArray(int i) {
            return new Server[i];
        }
    };
}
