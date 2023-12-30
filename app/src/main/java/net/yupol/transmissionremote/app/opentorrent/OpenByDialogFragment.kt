package net.yupol.transmissionremote.app.opentorrent

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.yupol.transmissionremote.app.R

class OpenByDialogFragment : DialogFragment() {

    private val listener: OnOpenTorrentSelectedListener
        get() = checkNotNull(activity as? OnOpenTorrentSelectedListener) {
            "Activity must implement ${OnOpenTorrentSelectedListener::class.java.simpleName}"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.open_torrent)
            .setItems(R.array.open_torrent_by_entries) { _, which ->
                if (which == 0) { // by file
                    listener.onOpenTorrentByFile()
                } else if (which == 1) { // by address
                    listener.onOpenTorrentByAddress()
                }
            }
        return builder.create()
    }

    interface OnOpenTorrentSelectedListener {
        fun onOpenTorrentByFile()
        fun onOpenTorrentByAddress()
    }
}
