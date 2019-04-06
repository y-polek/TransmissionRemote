package net.yupol.transmissionremote.app.opentorrent.model

import com.turn.ttorrent.common.Torrent
import net.yupol.transmissionremote.app.model.PriorityViewModel
import net.yupol.transmissionremote.model.Dir
import java.io.File

class TorrentFile(path: String) {

    val name: String
    val size: Long
    val files: Array<File>
    val rootDir: Dir

    init {
        val torrent = Torrent(File(path).readBytes(), false)
        name = torrent.name
        size = torrent.size
        files = torrent.files().map { File(it.file.path, it.size) }.toTypedArray()
        rootDir = Dir.createFileTree(
                files.map {
                    net.yupol.transmissionremote.model.json.File(it.path, it.length)
                }.toTypedArray())
    }

    fun selectAllFilesIn(dir: Dir) {
        dir.dirs.forEach { subDir -> selectAllFilesIn(subDir) }
        dir.fileIndices.forEach { index -> files[index].wanted = true }
    }

    fun selectNoneFilesIn(dir: Dir) {
        dir.dirs.forEach { subDir -> selectNoneFilesIn(subDir) }
        dir.fileIndices.forEach { index -> files[index].wanted = false }
    }

    private fun Torrent.files(): List<Torrent.TorrentFile> {
        val filesField = this::class.java.getDeclaredField("files")
        filesField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return filesField.get(this) as List<Torrent.TorrentFile>
    }

    data class File(
            val path: String,
            val length: Long,
            var wanted: Boolean = true,
            var priority: PriorityViewModel = PriorityViewModel.NORMAL)
    {
        val name: String by lazy {
            path.split('/').last()
        }
    }
}
