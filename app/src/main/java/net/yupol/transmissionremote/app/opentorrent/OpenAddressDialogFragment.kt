package net.yupol.transmissionremote.app.opentorrent

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Bundle
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import net.yupol.transmissionremote.app.R

class OpenAddressDialogFragment : DialogFragment() {

    private val listener: OnOpenMagnetListener
        get() = checkNotNull(activity as? OnOpenMagnetListener) {
            "Activity must implement ${OnOpenMagnetListener::class.java.simpleName}"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.open_address_dialog, null)
        val addressText = view.findViewById<TextInputEditText>(R.id.address_text)
        builder.setView(view)
            .setTitle(R.string.address_of_torrent_file)
            .setPositiveButton(R.string.open) { _, _ -> listener.onOpenMagnet(addressText.text.toString()) }
            .setNegativeButton(android.R.string.cancel, null)
        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
        if (clipboard != null) {
            val clipData = clipboard.primaryClip
            if (clipData != null && clipData.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                val text = clipData.getItemAt(0).text
                if (text != null) {
                    addressText.setText(text)
                    addressText.setSelection(0, addressText.text?.length ?: 0)
                }
            }
        }
        val dialog = builder.create()
        addressText.addTextChangedListener { s ->
            val openButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            openButton.isEnabled = !s.isNullOrBlank()
        }
        dialog.setOnShowListener {
            val openButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            openButton.isEnabled = !addressText.text.isNullOrBlank()
        }
        return dialog
    }

    interface OnOpenMagnetListener {
        fun onOpenMagnet(uri: String?)
    }
}
