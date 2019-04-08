package net.yupol.transmissionremote.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface ServerRepository {

    fun isTurtleModeEnabled(): Single<Boolean>

    fun setTurtleModeEnabled(enabled: Boolean): Completable

    fun defaultDownloadDir(): Single<String>
}
