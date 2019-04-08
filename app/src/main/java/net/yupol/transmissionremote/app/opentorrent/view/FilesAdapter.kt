package net.yupol.transmissionremote.app.opentorrent.view

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.*
import com.buildware.widget.indeterm.IndeterminateCheckBox
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.PriorityViewModel
import net.yupol.transmissionremote.app.opentorrent.model.TorrentFile
import net.yupol.transmissionremote.app.utils.*
import net.yupol.transmissionremote.model.Dir
import net.yupol.transmissionremote.utils.toArray

class FilesAdapter(
        private val torrentFile: TorrentFile,
        private val dir: Dir,
        private val listener: Listener): RecyclerView.Adapter<FilesAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.file_item_layout, parent, false)
        return ViewHolder(
                itemView,
                onClicked = { position ->
                    if (position.isDirPosition()) {
                        listener.onDirectorySelected(dirAt(position))
                    } else {
                        val file = fileAt(position)
                        parent.context.toast(file.name)
                    }
                },
                onPriorityClicked = { position ->
                    showPriorityPopup(itemView) { priority ->
                        if (position.isDirPosition()) {
                            dirAt(position).setPriority(priority)
                        } else {
                            fileAt(position).priority = priority
                        }
                        notifyItemChanged(position)
                    }
                },
                onChecked = { position, checked ->
                    if (position.isDirPosition()) {
                        dirAt(position).setWanted(checked)
                    } else {
                        fileAt(position).wanted = checked
                    }
                    notifyItemChanged(position)
                    listener.onFileSelectionChanged()
                }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position.isDirPosition()) {
            bindDir(holder, dirAt(position))
        } else {
            bindFile(holder, fileAt(position))
        }
    }

    override fun getItemCount() = dir.dirs.size + dir.fileIndices.size

    private fun bindDir(holder: ViewHolder, dir: Dir) {
        val wanted = dir.isWanted()
        val textColor = if (wanted != false) holder.primaryTextColor else holder.disabledTextColor

        holder.nameText.text = dir.name
        holder.nameText.setTextColor(textColor)
        holder.nameText.typeface = Typeface.DEFAULT_BOLD

        holder.sizeText.text = TextUtils.displayableSize(dir.size())
        holder.sizeText.setTextColor(textColor)

        holder.fileTypeImage.setImageResource(R.drawable.ic_folder)
        ImageViewCompat.setImageTintList(holder.fileTypeImage, ColorStateList.valueOf(textColor))

        val priorityIcon = holder.itemView.context.joinedDrawables(*dir.priorities().map { it.iconRes }.toArray())
        holder.priorityButton.setImageDrawable(priorityIcon)

        holder.setChecked(dir.isWanted())
    }

    private fun bindFile(holder: ViewHolder, file: TorrentFile.File) {
        val textColor = if (file.wanted) holder.primaryTextColor else holder.disabledTextColor

        holder.nameText.text = file.name
        holder.nameText.setTextColor(textColor)
        holder.nameText.typeface = Typeface.DEFAULT

        holder.sizeText.text = TextUtils.displayableSize(file.length)
        holder.sizeText.setTextColor(textColor)

        holder.fileTypeImage.setImageResource(file.icon())
        ImageViewCompat.setImageTintList(holder.fileTypeImage, ColorStateList.valueOf(textColor))

        holder.priorityButton.setImageResource(file.priority.iconRes)

        holder.setChecked(file.wanted)
    }

    private fun showPriorityPopup(itemView: View, prioritySelected: (PriorityViewModel) -> Unit) {
        val popup = ListPopupWindow(itemView.context)
        popup.isModal = true
        popup.setAdapter(PriorityAdapter)
        popup.setOnItemClickListener { parent, _, priorityPosition, _ ->
            popup.dismiss()
            val priority = parent.getItemAtPosition(priorityPosition) as PriorityViewModel
            prioritySelected(priority)
        }
        popup.anchorView = itemView
        val contentWidth = MetricsUtils.measurePopupSize(itemView.context, PriorityAdapter).width
        popup.setContentWidth(contentWidth)
        popup.horizontalOffset = itemView.width - contentWidth - itemView.context.resources.getDimensionPixelOffset(R.dimen.priority_popup_offset)
        popup.show()
    }

    private fun Int.isDirPosition() = this < dir.dirs.size

    private fun dirAt(position: Int): Dir {
        if (position < 0 || position >= dir.dirs.size) {
            throw IndexOutOfBoundsException("No subdirectory at position $position. " +
                    "# of subdirectories: ${dir.dirs.size}, # of files: ${dir.fileIndices.size}")
        }
        return dir.dirs[position]
    }

    private fun fileAt(position: Int): TorrentFile.File {
        if (position < dir.dirs.size || position >= (dir.dirs.size + dir.fileIndices.size)) {
            throw IndexOutOfBoundsException("No file at position $position. " +
                    "# of subdirectories: ${dir.dirs.size}, # of files: ${dir.fileIndices.size}")
        }
        val fileInDirPosition = position - dir.dirs.size
        val fileIndex = dir.fileIndices[fileInDirPosition]
        return torrentFile.files[fileIndex]
    }

    private fun Dir.size(): Long {
        val subDirsSize = dirs.map { it.size() }.sum()
        val filesSize = fileIndices.map { torrentFile.files[it].length }.sum()
        return subDirsSize + filesSize
    }

    private fun Dir.priorities(): List<PriorityViewModel> {
        val priorities = mutableSetOf<PriorityViewModel>()
        dirs.forEach {
            priorities.addAll(it.priorities())
        }
        fileIndices.forEach { fileIndex ->
            priorities.add(torrentFile.files[fileIndex].priority)
        }
        return priorities.sorted()
    }

    private fun Dir.setPriority(priority: PriorityViewModel) {
        dirs.forEach { it.setPriority(priority) }
        fileIndices.forEach { index -> torrentFile.files[index].priority = priority }
    }

    /**
     * @return 1) `true` if all files in directory (and subdirectories) are wanted
     * 2) `false` if all files in directory are unwanted
     * 3) `null` if directory contain both wanted and unwanted files
     */
    private fun Dir.isWanted(): Boolean? {
        var hasWanted = false
        var hasUnwanted = false

        for (subDir in dirs) {
            val isWanted = subDir.isWanted() ?: return null
            if (isWanted) {
                hasWanted = true
            } else {
                hasUnwanted = true
            }
            if (hasWanted and hasUnwanted) return null
        }

        for (fileIndex in fileIndices) {
            val file = torrentFile.files[fileIndex]
            if (file.wanted) {
                hasWanted = true
            } else {
                hasUnwanted = true
            }
            if (hasWanted and hasUnwanted) return null
        }
        return hasWanted
    }

    private fun Dir.setWanted(wanted: Boolean) {
        dirs.forEach { it.setWanted(wanted) }
        fileIndices.forEach { index ->
            torrentFile.files[index].wanted = wanted
        }
    }

    @DrawableRes
    private fun TorrentFile.File.icon(): Int = fileTypeIcon(name.extension())

    class ViewHolder(
            itemView: View,
            private val onClicked: (position: Int) -> Unit,
            private val onPriorityClicked: (position: Int) -> Unit,
            private val onChecked: (position: Int, checked: Boolean) -> Unit): RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.name_text) lateinit var nameText: TextView
        @BindView(R.id.size_text) lateinit var sizeText: TextView
        @BindView(R.id.file_type_icon) lateinit var fileTypeImage: ImageView
        @BindView(R.id.checkbox) lateinit var checkbox: IndeterminateCheckBox
        @BindView(R.id.priority_button) lateinit var priorityButton: ImageButton

        @BindColor(R.color.text_color_primary) @JvmField var primaryTextColor: Int = 0
        @BindColor(R.color.text_color_secondary) @JvmField var secondaryTextColor: Int = 0
        @BindColor(R.color.text_primary_disabled) @JvmField var disabledTextColor: Int = 0

        private var checkboxListenerDisabled = false

        init {
            ButterKnife.bind(this, itemView)
            checkbox.setOnStateChangedListener { _, isChecked ->
                if (checkboxListenerDisabled) return@setOnStateChangedListener
                isChecked ?: return@setOnStateChangedListener

                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnStateChangedListener

                onChecked(position, isChecked)
            }
        }

        fun setChecked(isChecked: Boolean?) {
            checkboxListenerDisabled = true
            checkbox.state = isChecked
            checkboxListenerDisabled = false
        }

        @OnClick(R.id.root_layout)
        fun onClicked() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClicked(position)
            }
        }

        @OnClick(R.id.priority_button)
        fun onPriorityClicked() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onPriorityClicked(position)
            }
        }
    }

    interface Listener {
        fun onDirectorySelected(dir: Dir)
        fun onFileSelectionChanged()
    }
}
