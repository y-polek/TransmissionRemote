package net.yupol.transmissionremote.app.model

import android.support.annotation.DrawableRes
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.data.model.ErrorType
import net.yupol.transmissionremote.data.model.ErrorType.*

data class TorrentViewModel(
        val id: Int,
        val name: String,
        val downloadedSize: Long,
        val totalSize: Long,
        val sizeWhenDone: Long,
        val completed: Boolean,
        val uploadedSize: Long,
        val uploadRatio: Double,
        val downloadRate: Long,
        val uploadRate: Long,
        val paused: Boolean,
        val rechecking: Boolean,
        val progressPercent: Double,
        val recheckProgressPercent: Double,
        val eta: Long,
        val errorMessage: String? = null,
        val errorType: ErrorType = ErrorType.NONE,
        val selected: Boolean)
{
    fun hasErrorOrWarning() = errorType != NONE

    @DrawableRes
    fun errorIcon(): Int {
        return when (errorType) {
            TRACKER_WARNING -> R.drawable.ic_warning
            TRACKER_ERROR, LOCAL_ERROR -> R.drawable.ic_error
            else -> 0
        }
    }
}
