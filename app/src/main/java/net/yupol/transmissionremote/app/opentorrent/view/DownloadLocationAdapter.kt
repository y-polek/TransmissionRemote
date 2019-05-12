package net.yupol.transmissionremote.app.opentorrent.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import net.yupol.transmissionremote.app.R

class DownloadLocationAdapter(
        private val pinned: Boolean,
        private val listener: Listener) : RecyclerView.Adapter<DownloadLocationAdapter.ViewHolder>()
{

    private var locations = listOf<String>()

    fun setLocations(locations: List<String>) {
        val diff = DiffUtil.calculateDiff(DiffCallback(this.locations, locations))
        this.locations = locations.toList()
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.download_location_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.locationText.text = locations[position]
    }

    override fun getItemCount() = locations.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.location_text) lateinit var locationText: TextView
        @BindView(R.id.pin_button) lateinit var pinButton: ImageButton

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    interface Listener {

        fun onLocationSelected(location: String)

        fun onLocationDeselected(location: String)

        fun onPinButtonClicked(location: String)
    }

    private class DiffCallback(private val oldItems: List<String>, private val newItems: List<String>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(oldItemPosition, newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].equals(newItems[newItemPosition], ignoreCase = true)
        }
    }
}
