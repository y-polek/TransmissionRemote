package net.yupol.transmissionremote.app.opentorrent

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindColor
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.turn.ttorrent.common.Torrent
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.utils.*
import net.yupol.transmissionremote.model.Dir
import net.yupol.transmissionremote.model.json.File

class FilesAdapter(
        torrentFile: Torrent,
        private val dir: Dir,
        private val listener: Listener): RecyclerView.Adapter<FilesAdapter.ViewHolder>()
{
    private val files by lazy { torrentFile.files() }
    private val fileStats by lazy { torrentFile.fileStats() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.file_item_layout, parent, false)
        return ViewHolder(itemView, onClick = { position ->
            if (position < dir.dirs.size) {
                listener.onDirectorySelected(dir.dirs[position])
            } else {
                val filePosition = position - dir.dirs.size
                val file = files[filePosition]
                parent.context.toast(file.name)
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < dir.dirs.size) {
            bindDir(holder, dir.dirs[position])
        } else {
            val filePosition = position - dir.dirs.size
            bindFile(holder, files[filePosition])
        }
    }

    override fun getItemCount() = dir.dirs.size + dir.fileIndices.size

    private fun bindDir(holder: ViewHolder, dir: Dir) {
        holder.nameText.text = dir.name
        holder.nameText.setTextColor(holder.primaryTextColor)

        holder.sizeText.text = TextUtils.displayableSize(dir.size())

        holder.fileTypeImage.setImageResource(R.drawable.ic_folder)
        ImageViewCompat.setImageTintList(holder.fileTypeImage, ColorStateList.valueOf(holder.primaryTextColor))
    }

    private fun bindFile(holder: ViewHolder, file: File) {
        holder.nameText.text = file.name
        holder.nameText.setTextColor(holder.secondaryTextColor)

        holder.sizeText.text = TextUtils.displayableSize(file.length)

        holder.fileTypeImage.setImageResource(file.icon())
        ImageViewCompat.setImageTintList(holder.fileTypeImage, ColorStateList.valueOf(holder.secondaryTextColor))
    }

    private fun Dir.size(): Long {
        val subDirsSize = dirs.map { it.size() }.sum()
        val filesSize = fileIndices.map { files[it].length }.sum()
        return subDirsSize + filesSize
    }

    class ViewHolder(itemView: View, private val onClick: (position: Int) -> Unit): RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.name_text) lateinit var nameText: TextView
        @BindView(R.id.size_text) lateinit var sizeText: TextView
        @BindView(R.id.file_type_icon) lateinit var fileTypeImage: ImageView
        @BindView(R.id.checkbox) lateinit var checkbox: CheckBox
        @BindView(R.id.priority_button) lateinit var priorityButton: ImageButton

        @BindColor(R.color.text_color_primary) @JvmField var primaryTextColor: Int = 0
        @BindColor(R.color.text_color_secondary) @JvmField var secondaryTextColor: Int = 0

        init {
            ButterKnife.bind(this, itemView)
        }

        @OnClick(R.id.root_layout)
        fun onClicked() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClick(position)
            }
        }
    }

    interface Listener {
        fun onDirectorySelected(dir: Dir)
    }
}
