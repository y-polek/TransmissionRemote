package net.yupol.transmissionremote.app.transport;

import android.os.Bundle;

import net.yupol.transmissionremote.app.BaseActivity;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.model.Server;

public abstract class BaseSpiceActivity extends BaseActivity {

    private final SpiceTransportManager transportManager = new SpiceTransportManager();
    private final TransmissionRemote.OnActiveServerChangedListener activeServerListener = new TransmissionRemote.OnActiveServerChangedListener() {
        @Override
        public void serverChanged(Server newServer) {
            transportManager.setServer(newServer);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TransmissionRemote app = (TransmissionRemote) getApplication();
        app.addOnActiveServerChangedListener(activeServerListener);
        transportManager.setServer(app.getActiveServer());

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        transportManager.start(this);

        super.onStart();
    }

    @Override
    protected void onStop() {
        transportManager.cancelAllRequests();
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
