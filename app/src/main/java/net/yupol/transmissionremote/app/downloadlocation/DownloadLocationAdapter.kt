package net.yupol.transmissionremote.app.downloadlocation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import net.yupol.transmissionremote.app.R

class DownloadLocationAdapter(
        private val isPinnable: Boolean,
        private val listener: Listener) : RecyclerView.Adapter<DownloadLocationAdapter.ViewHolder>()
{
    var locations: List<String> = emptyList()
        set(value) {
            field = value.sorted()
            notifyDataSetChanged()
        }

    override fun getItemCount() = locations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.download_location_item_layout, parent, false)

        return ViewHolder(itemView,
                onLocationClicked = { position ->
                    listener.onLocationSelected(locations[position])
                },
                onPinButtonClicked = { position ->
                    if (isPinnable) {
                        listener.onLocationPinned(locations[position])
                    } else {
                        listener.onLocationUnpinned(locations[position])
                    }
                })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.location.text = locations[position]
        holder.pinButton.setImageResource(if (isPinnable) R.drawable.ic_pin else R.drawable.ic_unpin)
    }

    class ViewHolder(
            itemView: View,
            private val onLocationClicked: (position: Int) -> Unit,
            private val onPinButtonClicked: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView)
    {
        @BindView(R.id.location_text) lateinit var location: TextView
        @BindView(R.id.pin_button) lateinit var pinButton: ImageView

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

            onPinButtonClicked(position)
        }
    }

    interface Listener {
        fun onLocationSelected(location: String)

        fun onLocationPinned(location: String)

        fun onLocationUnpinned(location: String)
    }
}
