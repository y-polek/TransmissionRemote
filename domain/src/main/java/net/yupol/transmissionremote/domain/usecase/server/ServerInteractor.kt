package net.yupol.transmissionremote.domain.usecase.server

import io.reactivex.Single
import javax.inject.Inject

class ServerInteractor @Inject constructor(private val turtleMode: TurtleMode) {

    fun isTurtleModeEnabled(): Single<Boolean> {
        return turtleMode.isEnabled()
    }

    fun setTurtleModeEnabled(enabled: Boolean): Single<Boolean> {
        return turtleMode.setEnabled(enabled)
    }
}
