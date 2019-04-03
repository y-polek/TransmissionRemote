package net.yupol.transmissionremote.app.utils

import com.turn.ttorrent.common.Torrent
import net.yupol.transmissionremote.model.Priority
import net.yupol.transmissionremote.model.json.File
import net.yupol.transmissionremote.model.json.FileStat

fun Torrent.files(): Array<File> {
    return torrentFiles().map { File(it.file.path, it.size) }.toTypedArray()
}

private fun Torrent.torrentFiles(): List<Torrent.TorrentFile> {
    val filesField = this::class.java.getDeclaredField("files")
    filesField.isAccessible = true
    return filesField.get(this) as List<Torrent.TorrentFile>
}
