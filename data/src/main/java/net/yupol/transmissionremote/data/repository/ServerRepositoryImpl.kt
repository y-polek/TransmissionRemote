package net.yupol.transmissionremote.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.data.api.TransmissionRpcApi
import net.yupol.transmissionremote.domain.repository.ServerRepository
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor(private val api: TransmissionRpcApi): ServerRepository {

    override fun isTurtleModeEnabled(): Single<Boolean> {
        return api.isTurtleModeEnabled()
    }

    override fun setTurtleModeEnabled(enabled: Boolean): Completable {
        return api.setServerSettings(mapOf("alt-speed-enabled" to enabled))
    }
}
