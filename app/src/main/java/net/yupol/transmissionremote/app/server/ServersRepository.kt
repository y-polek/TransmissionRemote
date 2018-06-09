package net.yupol.transmissionremote.app.server

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import net.yupol.transmissionremote.model.Server
import javax.inject.Inject

class ServersRepository @Inject constructor(private val storage: ServersStorage) {

    private val servers = MutableLiveData<List<Server>>()
    private val activeServer = MutableLiveData<Server>()

    init {
        servers.value = storage.readServers()
    }

    fun getServers(): LiveData<List<Server>> = servers

    fun addServer(server: Server) {
        val newServers = servers.value!! + server
        servers.value = newServers
        storage.writeServers(newServers)
    }

    fun removeServer(server: Server) {
        val newServers = servers.value!! - server
        servers.value = newServers
        storage.writeServers(newServers)
    }

    fun getActiveServer(): LiveData<Server> = activeServer

    fun setActiveServer(server: Server) {
        activeServer.value = server
        storage.writeActiveServer(server)
    }
}