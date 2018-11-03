package net.yupol.transmissionremote.domain.repository

import io.reactivex.Observable
import net.yupol.transmissionremote.domain.model.Server

interface ServerRepository {

    fun activeServer(): Observable<Server>

    fun setActiveServer(server: Server)

    fun servers(): Observable<List<Server>>

    fun addServer(server: Server)

    fun removeServer(server: Server)
}
