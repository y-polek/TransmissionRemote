package net.yupol.transmissionremote.app.transport

import android.os.Bundle
import net.yupol.transmissionremote.app.BaseActivity
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.TransmissionRemote.OnActiveServerChangedListener
import net.yupol.transmissionremote.app.server.Server
import net.yupol.transmissionremote.app.transport.okhttp.OkHttpTransportManager

abstract class BaseSpiceActivity : BaseActivity() {

    private var transportManager: TransportManager? = null
    private val activeServerListener = OnActiveServerChangedListener { newServer: Server? ->
        transportManager = updatedTransportManager(newServer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as TransmissionRemote
        app.addOnActiveServerChangedListener(activeServerListener)
        transportManager = updatedTransportManager(app.activeServer)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        (transportManager as? SpiceTransportManager)?.start(this)
        super.onStart()
    }

    override fun onStop() {
        (transportManager as? SpiceTransportManager)?.apply {
            if (isStarted) {
                cancelAllRequests()
                shouldStop()
            }
        }
        super.onStop()
    }

    override fun onDestroy() {
        val app = application as TransmissionRemote
        app.removeOnActiveServerChangedListener(activeServerListener)
        super.onDestroy()
    }

    fun getTransportManager(): TransportManager {
        return checkNotNull(transportManager) { "Transport manager is not initialized yet" }
    }

    private fun updatedTransportManager(server: Server?): TransportManager? {
        if (server == null) {
            return null
        }
        if (TransmissionRemote.getInstance().featureManager.useOkHttp()) {
            return OkHttpTransportManager(server)
        }
        val spiceTransportManager = (transportManager as? SpiceTransportManager) ?: SpiceTransportManager()
        spiceTransportManager.setServer(server)
        return spiceTransportManager
    }
}
