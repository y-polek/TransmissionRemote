package net.yupol.transmissionremote.app.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.data.model.ErrorType
import net.yupol.transmissionremote.data.model.ErrorType.*

@Parcelize
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
        val selected: Boolean): Parcelable
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

fun Iterable<TorrentViewModel>.totalDownloadSpeed(): Long {
    return map(TorrentViewModel::downloadRate).sum()
}

fun Iterable<TorrentViewModel>.totalUploadSpeed(): Long {
    return map(TorrentViewModel::uploadRate).sum()
}
