package net.yupol.transmissionremote.domain.repository

import io.reactivex.Observable
import net.yupol.transmissionremote.domain.model.Server

interface ServerListRepository {

    fun activeServer(): Observable<Server>

    fun setActiveServer(server: Server)

    fun servers(): Observable<List<Server>>

    fun addServer(server: Server)

    fun removeServer(withName: String)

    fun findServer(withName: String): Server?

    fun updateServer(withName: String, server: Server)
}
