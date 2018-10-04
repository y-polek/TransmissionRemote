package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.data.api.model.FileEntity
import net.yupol.transmissionremote.data.api.model.FileStatEntity
import net.yupol.transmissionremote.model.json.File
import net.yupol.transmissionremote.model.json.FileStat

object FileMapper {

    @JvmStatic
    fun toViewModel(entity: FileEntity): File {
        return File(entity.path, entity.length)
    }

    @JvmStatic
    fun Array<FileEntity>.toViewModel(): Array<File> {
        return map(FileMapper::toViewModel).toTypedArray()
    }

    @JvmStatic
    fun toViewModel(entity: FileStatEntity): FileStat {
        return FileStat(entity.wanted, entity.priority, entity.bytesCompleted)
    }

    @JvmStatic
    fun Array<FileStatEntity>.toViewModel(): Array<FileStat> {
        return map(FileMapper::toViewModel).toTypedArray()
    }
}
