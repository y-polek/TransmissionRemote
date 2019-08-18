package net.yupol.transmissionremote.app.downloadlocation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import net.yupol.transmissionremote.app.R

class DownloadLocationAdapter(private val listener: Listener) : RecyclerView.Adapter<DownloadLocationAdapter.ViewHolder>() {

    var locations: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = locations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.download_location_item_layout, parent, false)

        return ViewHolder(itemView) { position ->
            listener.onLocationSelected(locations[position])
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.location.text = locations[position]
    }

    class ViewHolder(
            itemView: View,
            private val onLocationClicked: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView)
    {
        @BindView(R.id.location_text) lateinit var location: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        @OnClick(R.id.location_text)
        fun onLocationClicked() {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return

            onLocationClicked(position)
        }

        @OnClick(R.id.pin_button)
        fun onPinButtonClicked() {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return


        }
    }

    interface Listener {
        fun onLocationSelected(location: String)

        fun onLocationPinned(location: String)

        fun onLocationUnpinned(location: String)
    }
}
