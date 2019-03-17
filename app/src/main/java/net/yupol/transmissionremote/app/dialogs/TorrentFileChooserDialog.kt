package net.yupol.transmissionremote.app.dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.FileFilter
import com.afollestad.materialdialogs.files.fileChooser
import net.yupol.transmissionremote.app.R
import java.io.File

class TorrentFileChooserDialog: DialogFragment() {

    private val initDir: File by lazy {
        val initDirPath = arguments?.getString(KEY_INIT_DIR_PATH)
        return@lazy when {
            initDirPath != null -> File(initDirPath)
            else -> Environment.getExternalStorageDirectory()
        }
    }

    private val listener: Listener? by lazy {
        return@lazy activity as? Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return MaterialDialog(requireContext()).show {
            fileChooser(initialDirectory = initDir, filter = FILE_FILTER) { _, file ->
                listener?.onTorrentFileChosen(file)
            }
            positiveButton(R.string.add)
            negativeButton(R.string.cancel)
        }
    }

    interface Listener {
        fun onTorrentFileChosen(file: File)
    }

    companion object {

        private val FILE_FILTER: FileFilter = {
            it.isDirectory || it.extension.equals("torrent", ignoreCase = true)
        }
        private const val KEY_INIT_DIR_PATH = "key_init_dir_path"

        fun instance(initDirPath: String?): TorrentFileChooserDialog {
            return TorrentFileChooserDialog().apply {
                arguments = bundleOf(KEY_INIT_DIR_PATH to initDirPath)
            }
        }
    }
}
