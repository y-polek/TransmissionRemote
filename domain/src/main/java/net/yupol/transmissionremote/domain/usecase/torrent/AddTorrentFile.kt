package net.yupol.transmissionremote.domain.usecase.torrent

import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.AddTorrentResult
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import java.io.File
import javax.inject.Inject

class AddTorrentFile @Inject constructor(private val repo: TorrentListRepository) {

    fun execute(file: File, destinationDir: String, paused: Boolean, filesUnwanted: List<Int>): Single<AddTorrentResult> {
        return repo.addTorrentFile(file, destinationDir, paused, filesUnwanted)
    }
}
