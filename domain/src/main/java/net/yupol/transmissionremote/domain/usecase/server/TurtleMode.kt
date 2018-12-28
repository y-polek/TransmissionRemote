package net.yupol.transmissionremote.domain.usecase.server

import io.reactivex.Single
import net.yupol.transmissionremote.domain.repository.ServerRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TurtleMode @Inject constructor(private val repo: ServerRepository) {

    fun isEnabled(): Single<Boolean> {
        return repo.isTurtleModeEnabled()
    }

    fun setEnabled(enabled: Boolean): Single<Boolean> {
        return repo.setTurtleModeEnabled(enabled)
                .andThen(Single.defer {
                    isEnabled().delaySubscription(500, TimeUnit.MILLISECONDS)
                })
    }
}
