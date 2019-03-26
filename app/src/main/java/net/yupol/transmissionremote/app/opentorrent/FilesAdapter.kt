package net.yupol.transmissionremote.app.opentorrent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.turn.ttorrent.common.Torrent
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.utils.TextUtils
import net.yupol.transmissionremote.app.utils.fileStats
import net.yupol.transmissionremote.app.utils.files
import net.yupol.transmissionremote.model.Dir
import net.yupol.transmissionremote.model.json.File

class FilesAdapter(torrentFile: Torrent, private val dir: Dir): RecyclerView.Adapter<FilesAdapter.ViewHolder>() {

    private val files by lazy { torrentFile.files() }
    private val fileStats by lazy { torrentFile.fileStats() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.file_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == RecyclerView.NO_POSITION) return

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
        holder.sizeText.text = TextUtils.displayableSize(dir.size())
        holder.fileTypeImage.setImageResource(R.drawable.ic_folder)
    }

    private fun bindFile(holder: ViewHolder, file: File) {
        holder.nameText.text = file.name
        holder.sizeText.text = TextUtils.displayableSize(file.length)
    }

    private fun Dir.size(): Long {
        val subDirsSize = dirs.map { it.size() }.sum()
        val filesSize = fileIndices.map { files[it].length }.sum()
        return subDirsSize + filesSize
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.name_text) lateinit var nameText: TextView
        @BindView(R.id.size_text) lateinit var sizeText: TextView
        @BindView(R.id.file_type_icon) lateinit var fileTypeImage: ImageView
        @BindView(R.id.checkbox) lateinit var checkbox: CheckBox
        @BindView(R.id.priority_button) lateinit var priorityButton: ImageButton

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
