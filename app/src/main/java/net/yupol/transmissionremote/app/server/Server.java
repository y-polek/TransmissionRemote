package net.yupol.transmissionremote.app.server;

import static com.google.common.base.Preconditions.*;

public class Server {

    private String name;
    private String host;
    private int port;
    private String userName;
    private String password;

    public Server(String name, String host, int port, String userName, String password) {
        checkNotNull(name, "Name must be non null");
        this.name = name;
        this.host = host;
        this.port = port;
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
}
