package net.yupol.transmissionremote.domain.model

class AddTorrentResult private constructor(
        val success: Boolean = false,
        val duplicate: Boolean = false,
        val error: Boolean = false,
        val errorMessage: String? = null)
{
    companion object {
        fun success() = AddTorrentResult(success = true)
        fun duplicate() = AddTorrentResult(duplicate = true)
        fun error(message: String? = null) = AddTorrentResult(error = true, errorMessage = message)
    }
}
