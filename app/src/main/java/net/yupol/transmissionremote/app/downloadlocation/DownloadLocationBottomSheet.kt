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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.download_location_bottom_sheet, container, false)
        ButterKnife.bind(this, view)

        defaultLocation.text = "~/Downloads"

        val adapter = DownloadLocationAdapter(this)
        adapter.locations = listOf(
                "~/",
                "~/Download",
                "/Users/yury/home/Desktop",
                "/Users/yury/home/Desktop",
                "/Users/yury/Documents/iCloud/absdasdf/asdf/aasfa /asfasfasfa fsa/asdfaasfasdf/Src/TransmissionRemote/app/build/outputs/apk")
        pinnedLocationsRecyclerView.adapter = adapter
        previousLocationsRecyclerView.adapter = adapter

        return view
    }

    @OnClick(R.id.default_location_text)
    fun onDefaultLocationSelected() {
        listener?.onLocationSelected(repo.defaultDownloadLocation())
    }

    override fun onLocationSelected(location: String) {
        listener?.onLocationSelected(location)
    }

    override fun onLocationPinned(location: String) {
        TODO("not implemented")
    }

    override fun onLocationUnpinned(location: String) {
        TODO("not implemented")
    }

    companion object {
        const val TAG = "DownloadLocationBottomSheet"

        fun instance() = DownloadLocationBottomSheet()
    }
}
