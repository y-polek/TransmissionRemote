package net.yupol.transmissionremote.app.downloadlocation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.domain.repository.DownloadLocationRepository
import javax.inject.Inject

class DownloadLocationBottomSheet : BottomSheetDialogFragment(), DownloadLocationAdapter.Listener {

    @Inject lateinit var repo: DownloadLocationRepository

    @BindView(R.id.default_location_text) lateinit var defaultLocation: TextView
    @BindView(R.id.pinned_locations_list) lateinit var pinnedLocationsRecyclerView: RecyclerView
    @BindView(R.id.previous_locations_list) lateinit var previousLocationsRecyclerView: RecyclerView

    private lateinit var historyAdapter: DownloadLocationAdapter
    private lateinit var pinnedAdapter: DownloadLocationAdapter

    private var listener: OnLocationSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLocationSelectedListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().appComponent().serverManager().serverComponent?.inject(this)
        super.onCreate(savedInstanceState)

        historyAdapter = DownloadLocationAdapter(isPinnable = true, listener = this)
        historyAdapter.locations = repo.getPreviousLocations()

        pinnedAdapter = DownloadLocationAdapter(isPinnable = false, listener = this)
        pinnedAdapter.locations = repo.getPinnedLocations()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.download_location_bottom_sheet, container, false)
        ButterKnife.bind(this, view)

        defaultLocation.text = repo.getDefaultDownloadLocation()

        previousLocationsRecyclerView.adapter = historyAdapter
        pinnedLocationsRecyclerView.adapter = pinnedAdapter

        return view
    }

    @OnClick(R.id.default_location_text)
    fun onDefaultLocationSelected() {
        listener?.onLocationSelected(repo.getDefaultDownloadLocation())
        dismiss()
    }

    override fun onLocationSelected(location: String) {
        listener?.onLocationSelected(location)
        dismiss()
    }

    override fun onLocationPinned(location: String) {
        repo.pinLocation(location)
        pinnedAdapter.locations = repo.getPinnedLocations()
    }

    override fun onLocationUnpinned(location: String) {
        repo.unpinLocation(location)
        pinnedAdapter.locations = repo.getPinnedLocations()
    }

    companion object {
        const val TAG = "DownloadLocationBottomSheet"

        fun instance() = DownloadLocationBottomSheet()
    }
}
