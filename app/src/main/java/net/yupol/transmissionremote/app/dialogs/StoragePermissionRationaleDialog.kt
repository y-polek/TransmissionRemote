package net.yupol.transmissionremote.app.dialogs

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import net.yupol.transmissionremote.app.R

class StoragePermissionRationaleDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialDialog(requireContext()).apply {
            message(R.string.storage_permission_never_ask_again_message)
            positiveButton(R.string.app_settings) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                if (intent.resolveActivity(context.packageManager) != null) {
                    startActivity(intent)
                }
            }
            negativeButton(android.R.string.cancel)
        }
    }

    companion object {

        fun instance() = StoragePermissionRationaleDialog()
    }
}
