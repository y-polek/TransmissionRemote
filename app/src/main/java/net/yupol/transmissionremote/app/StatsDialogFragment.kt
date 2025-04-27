package net.yupol.transmissionremote.app

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.octo.android.robospice.persistence.exception.SpiceException
import com.octo.android.robospice.request.listener.RequestListener
import net.yupol.transmissionremote.app.databinding.StatsBinding
import net.yupol.transmissionremote.app.model.json.ServerStats
import net.yupol.transmissionremote.app.opentorrent.DownloadLocationDialogFragment
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity
import net.yupol.transmissionremote.app.transport.TransportManager
import net.yupol.transmissionremote.app.transport.request.StatsGetRequest
import android.text.format.Formatter

class StatsDialogFragment : DialogFragment() {

    private val TAG: String = StatsDialogFragment::class.java.simpleName

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = DataBindingUtil.inflate<StatsBinding>(inflater, R.layout.stats, null, false)
            binding.loadingInProgress = true

            builder.setView(binding.root)
                .setNegativeButton(R.string.material_drawer_close,
                    {dialog, id -> getDialog()?.cancel()})

            getTransportManager().doRequest(StatsGetRequest(), object : RequestListener<ServerStats> {
                override fun onRequestFailure(spiceException: SpiceException?) {
                    Log.e(TAG, "error fetching server stats")
                }

                override fun onRequestSuccess(result: ServerStats?) {
                    binding.statsTotalUploaded.text =
                            Formatter.formatFileSize(it, result?.cumulativeStats?.uploadedBytes?: 0)
                    binding.statsTotalDownloaded.text =
                        Formatter.formatFileSize(it, result?.cumulativeStats?.downloadedBytes?: 0)

                    binding.statsSessionUploaded.text =
                        Formatter.formatFileSize(it, result?.currentStats?.uploadedBytes?: 0)
                    binding.statsSessionDownloaded.text =
                        Formatter.formatFileSize(it, result?.currentStats?.downloadedBytes?: 0)

                    binding.loadingInProgress = false
                }

            })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun getTransportManager(): TransportManager {
        return (activity as BaseSpiceActivity).getTransportManager()
    }
}