package net.yupol.transmissionremote.app.transport;

import android.app.Activity;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;

public class BaseSpiceActivity extends Activity {

    private final SpiceTransportManager transportManager = new SpiceTransportManager();
    private final TransmissionRemote.OnActiveServerChangedListener activeServerListener = new TransmissionRemote.OnActiveServerChangedListener() {
        @Override
        public void serverChanged(Server newServer) {
            transportManager.setServer(newServer);
        }
    };

    @Override
    protected void onStart() {
        TransmissionRemote app = (TransmissionRemote) getApplication();
        transportManager.setServer(app.getActiveServer());
        app.addOnActiveServerChangedListener(activeServerListener);

        transportManager.start(this);

        super.onStart();
    }

    @Override
    protected void onStop() {
        transportManager.shouldStop();

        TransmissionRemote app = (TransmissionRemote) getApplication();
        app.removeOnActiveServerChangedListener(activeServerListener);

        super.onStop();
    }

    public TransportManager getTransportManager() {
        return transportManager;
    }
}
