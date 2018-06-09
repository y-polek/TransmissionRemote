package net.yupol.transmissionremote.app.server

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import net.yupol.transmissionremote.model.Server
import javax.inject.Inject

class ServersRepository @Inject constructor() {

    private val servers = MutableLiveData<List<Server>>()
    private val activeServer = MutableLiveData<Server>()

    init {
        servers.value = listOf()
    }

    fun getServers(): LiveData<List<Server>> = servers

    fun addServer(server: Server) {
        servers.value = servers.value!! + server
    }

    fun removeServer(server: Server) {
        servers.value = servers.value!! - server
    }

    fun getActiveServer(): LiveData<Server> = activeServer

    fun setActiveServer(server: Server) {
        activeServer.value = server
    }
}