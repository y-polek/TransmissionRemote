package net.yupol.transmissionremote.app.torrentlist

import io.reactivex.Observable
import net.yupol.transmissionremote.model.json.Torrent
import net.yupol.transmissionremote.transport.NoNetworkException
import net.yupol.transmissionremote.transport.Transport
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TorrentsRepository @Inject constructor(private val transport: Transport) {

    fun torrents(): Observable<List<Torrent>> {

        return transport.api.torrentList()
                .doOnSuccess {
                    Timber.tag("TorrentsList").d("Single onSuccess: $it")
                }
                .doOnError {error ->
                    Timber.tag("TorrentsList").d("Single onError: ${error.message}")
                }
                .toObservable()
                .repeatWhen { handler ->
                    handler.delay(3, TimeUnit.SECONDS)
                            .flatMap { _ ->
                                Observable.just("")
                            }
                }
                .doOnNext {
                    Timber.tag("TorrentsList").d("Repository onNext: $it")
                }
    }
}