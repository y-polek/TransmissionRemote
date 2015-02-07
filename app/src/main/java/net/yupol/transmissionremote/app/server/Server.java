package net.yupol.transmissionremote.app.server;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;

public class Server implements Parcelable {

    public static final String TAG = Server.class.getSimpleName();

    private String name;
    private String host;
    private int port;
    private boolean useAuthentication;
    private String userName;
    private String password;

    public Server(@Nonnull String name, @Nonnull String host, int port) {
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

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("name", name);
            obj.put("host", host);
            obj.put("port", port);
            obj.put("useAuthentication", useAuthentication);
            obj.putOpt("userName", userName);
            obj.putOpt("password", password);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON object from Server object: " + this, e);
        }

        return obj;
    }

    public static Server fromJson(JSONObject obj) {
        try {
            String name = obj.getString("name");
            String host = obj.getString("host");
            int port = obj.getInt("port");
            boolean useAuthentication = obj.getBoolean("useAuthentication");
            if (useAuthentication) {
                String userName = obj.optString("userName", null);
                String password = obj.optString("password", null);
                return new Server(name, host, port, userName, password);
            } else {
                return new Server(name, host, port);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create Server object from JSON object: " + obj, e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (port != server.port) return false;
        if (useAuthentication != server.useAuthentication) return false;
        if (!host.equals(server.host)) return false;
        if (!name.equals(server.name)) return false;
        if (password != null ? !password.equals(server.password) : server.password != null)
            return false;
        if (userName != null ? !userName.equals(server.userName) : server.userName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + port;
        result = 31 * result + (useAuthentication ? 1 : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
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
