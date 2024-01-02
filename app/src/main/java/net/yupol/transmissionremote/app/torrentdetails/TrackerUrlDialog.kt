package net.yupol.transmissionremote.app.torrentdetails

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.databinding.TrackerUrlDialogLayoutBinding
import net.yupol.transmissionremote.app.model.json.TrackerStats

class TrackerUrlDialog : DialogFragment() {

    private val tracker: TrackerStats? by lazy {
        arguments?.getParcelable(KEY_TRACKER)
    }
    private val listener: OnTrackerUrlEnteredListener by lazy {
        checkNotNull(parentFragment as? OnTrackerUrlEnteredListener ?: activity as? OnTrackerUrlEnteredListener) {
            "Parent fragment or activity must implement OnTrackerUrlEnteredListener"
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DataBindingUtil.inflate<TrackerUrlDialogLayoutBinding>(
            layoutInflater, R.layout.tracker_url_dialog_layout, null, false
        )
        val edit = tracker != null
        @StringRes val positiveButtonRes = if (edit) {
            R.string.trackers_done_button
        } else {
            R.string.trackers_add_button
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (edit) R.string.trackers_edit_tracker_title else R.string.trackers_add_tracker_title)
            .setView(binding.root)
            .setPositiveButton(positiveButtonRes) { _, _ ->
                val url = formatUrl(binding.url.text.toString())
                listener.onTrackerUrlEntered(tracker, url)
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
        dialog.setOnShowListener {
            val url = formatUrl(binding.url.text.toString())
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).isEnabled = isValidUrl(url)
        }
        binding.url.doAfterTextChanged { s ->
            val addButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            if (addButton != null) {
                val url = formatUrl(s.toString())
                addButton.isEnabled = isValidUrl(url)
            }
        }
        if (edit) {
            val text = tracker?.announce.orEmpty().ifBlank {
                tracker?.host.orEmpty()
            }
            binding.url.setText(text)
        }
        return dialog
    }

    private fun formatUrl(url: String): String {
        return url.trim().lowercase().let {
            if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("udp://")) {
                it
            } else {
                "http://$it"
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return url.isNotBlank()
    }

    interface OnTrackerUrlEnteredListener {
        fun onTrackerUrlEntered(tracker: TrackerStats?, url: String?)
    }

    companion object {
        private const val KEY_TRACKER = "key_tracker"
        @JvmStatic
        fun newInstance(trackerStats: TrackerStats?): TrackerUrlDialog {
            val args = Bundle()
            args.putParcelable(KEY_TRACKER, trackerStats)
            val fragment = TrackerUrlDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
