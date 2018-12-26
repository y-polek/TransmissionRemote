package net.yupol.transmissionremote.app.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.TorrentViewModel

class RenameTorrentDialog: DialogFragment() {

    private val torrent: TorrentViewModel by lazy { arguments!![KEY_TORRENT] as TorrentViewModel }

    private val listener: Listener? by lazy {
        return@lazy if (activity is Listener) activity as Listener else null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(context!!).apply {
            message(R.string.rename_file)
            input(prefill = torrent.name,
                    inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS,
                    waitForPositiveButton = false) { dialog, text ->
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, text.isValidName())
            }
            positiveButton(R.string.rename) { dialog ->
                val name = dialog.getInputField()!!.text.toName()
                listener?.onTorrentNameSelected(torrent, name)
            }
            negativeButton(R.string.cancel)
            setActionButtonEnabled(WhichButton.POSITIVE, false)
        }
    }

    private fun CharSequence.toName(): String = trim().toString()

    private fun CharSequence.isValidName(): Boolean {
        val name = toName()
        return name.isNotEmpty() && name != torrent.name
    }

    interface Listener {
        fun onTorrentNameSelected(torrent: TorrentViewModel, newName: String)
    }

    companion object {

        private const val KEY_TORRENT = "key_torrent"

        fun instance(torrent: TorrentViewModel): RenameTorrentDialog {
            return RenameTorrentDialog().apply {
                arguments = bundleOf(KEY_TORRENT to torrent)
            }
        }
    }
}
