package net.yupol.transmissionremote.app.preferences.server

import net.yupol.transmissionremote.app.server.Server

data class ServersListViewState(
    val servers: List<Server> = emptyList(),
    val selectedServerId: String? = null
)
