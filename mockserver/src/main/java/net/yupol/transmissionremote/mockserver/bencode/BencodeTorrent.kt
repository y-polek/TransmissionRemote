package net.yupol.transmissionremote.mockserver.bencode

data class BencodeTorrent(
    val info: BencodeTorrentInfo,
    val comment: String,
    val creationDate: Long,
    val createdBy: String?
) {
    val totalSize: Long
        get() = info.length ?: info.files?.sumOf(BencodeFile::length) ?: 0

    companion object {
        fun fromMap(map: Map<String, Any?>): BencodeTorrent {
            return BencodeTorrent(
                info = BencodeTorrentInfo.fromMap(map["info"] as Map<String, Any?>),
                comment = map["comment"] as String,
                creationDate = map["creation date"] as Long,
                createdBy = map["created by"] as String?
            )
        }
    }
}

data class BencodeTorrentInfo(
    val name: String,
    val length: Long?,
    val files: List<BencodeFile>?
) {
    companion object {
        fun fromMap(infoMap: Map<String, Any?>): BencodeTorrentInfo {
            return BencodeTorrentInfo(
                name = infoMap["name"] as String,
                length = infoMap["length"] as Long?,
                files = (infoMap["files"] as? List<Map<String, Any>>)?.map(BencodeFile::fromMap)
            )
        }
    }
}

data class BencodeFile(
    val length: Long,
    val path: List<String>
) {
    companion object {
        fun fromMap(fileMap: Map<String, Any>): BencodeFile {
            return BencodeFile(
                length = fileMap["length"] as Long,
                path = fileMap["path"] as List<String>
            )
        }
    }
}
