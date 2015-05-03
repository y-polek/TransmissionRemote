package net.yupol.transmissionremote.app.transport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;

public class BaseSpiceActivity extends AppCompatActivity {

    private final SpiceTransportManager transportManager = new SpiceTransportManager();
    private final TransmissionRemote.OnActiveServerChangedListener activeServerListener = new TransmissionRemote.OnActiveServerChangedListener() {
        @Override
        public void serverChanged(Server newServer) {
            transportManager.setServer(newServer);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransmissionRemote app = (TransmissionRemote) getApplication();
        app.addOnActiveServerChangedListener(activeServerListener);

        transportManager.setServer(app.getActiveServer());
    }

    @Override
    protected void onStart() {
        transportManager.start(this);

        super.onStart();
    }

    @Override
    protected void onStop() {
        transportManager.shouldStop();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        TransmissionRemote app = (TransmissionRemote) getApplication();
        app.removeOnActiveServerChangedListener(activeServerListener);

        super.onDestroy();
    }

    public TransportManager getTransportManager() {
        return transportManager;
    }
}
