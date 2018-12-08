package net.yupol.transmissionremote.app.home

import android.graphics.PorterDuff
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.TorrentViewModel
import net.yupol.transmissionremote.app.torrentlist.PlayPauseButton
import net.yupol.transmissionremote.app.utils.ColorUtils
import net.yupol.transmissionremote.app.utils.TextUtils.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TorrentAdapter(private val listener: TorrentAdapter.ClickListener): RecyclerView.Adapter<TorrentAdapter.ViewHolder>() {

    companion object {
        private val ETA_INFINITE_THRESHOLD = TimeUnit.DAYS.toSeconds(7)
    }

    private var torrents = mutableListOf<TorrentViewModel>()

    init {
        setHasStableIds(true)
    }

    fun setTorrents(torrents: List<TorrentViewModel>) {
        val diffResult = DiffUtil.calculateDiff(TorrentsDiffCallback(newTorrents = torrents, oldTorrents = this.torrents))
        this.torrents = ArrayList(torrents)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTorrents(vararg updatedTorrents: TorrentViewModel) {
        updatedTorrents.forEach { updatedTorrent ->
            val idx = torrents.indexOfFirst { it.id == updatedTorrent.id }
            if (idx >= 0) {
                torrents[idx] = updatedTorrent
                notifyItemChanged(idx)
            }
        }
    }

    fun updateTorrentWithId(id: Int) {
        val idx = torrents.indexOfFirst { it.id == id }
        if (idx >= 0) notifyItemChanged(idx)
    }

    override fun getItemCount() = torrents.size

    override fun getItemId(position: Int) = torrents[position].id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.torrent_list_item, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == NO_POSITION) return
        holder.bind(torrents[position])
    }

    class ViewHolder(itemView: View, private val listener: ClickListener): RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.name) lateinit var name: TextView
        @BindView(R.id.downloaded_text) lateinit var downloadedText: TextView
        @BindView(R.id.uploaded_text) lateinit var uploadedText: TextView
        @BindView(R.id.download_rate) lateinit var downloadRateText: TextView
        @BindView(R.id.upload_rate) lateinit var uploadRateText: TextView
        @BindView(R.id.progress_bar) lateinit var progressBar: ProgressBar
        @BindView(R.id.percent_done_text) lateinit var percentDoneText: TextView
        @BindView(R.id.remaining_time_text) lateinit var remainingTimeText: TextView
        @BindView(R.id.error_message) lateinit var errorText: TextView
        @BindView(R.id.pause_resume_button) lateinit var pauseButton: PlayPauseButton
        @BindView(R.id.selection_overlay) lateinit var selectionOverlay: View

        private lateinit var torrent: TorrentViewModel

        init {
            ButterKnife.bind(this, itemView)
        }

        fun bind(torrent: TorrentViewModel) {
            this.torrent = torrent
            val context = itemView.context

            name.text = torrent.name

            downloadedText.text = if (torrent.completed) {
                if (torrent.sizeWhenDone == torrent.totalSize) displayableSize(torrent.totalSize)
                else context.getString(R.string.downloaded_text, displayableSize(torrent.sizeWhenDone), displayableSize(torrent.totalSize))
            } else {
                context.getString(R.string.downloaded_text, displayableSize(torrent.downloadedSize), displayableSize(torrent.sizeWhenDone))
            }

            uploadedText.text = context.getString(R.string.uploaded_text, displayableSize(torrent.uploadedSize), torrent.uploadRatio)

            downloadRateText.text = speedText(torrent.downloadRate)
            uploadRateText.text = speedText(torrent.uploadRate)

            val progressbarDrawable = when {
                torrent.paused -> R.drawable.torrent_progressbar_disabled
                torrent.rechecking -> R.drawable.torrent_progressbar_rechecking
                torrent.completed -> R.drawable.torrent_progressbar_finished
                else -> R.drawable.torrent_progressbar
            }
            progressBar.progressDrawable = ContextCompat.getDrawable(itemView.context, progressbarDrawable)
            val progress = if (torrent.rechecking) torrent.recheckProgressPercent else torrent.progressPercent
            progressBar.progress = (progressBar.max * progress).toInt()

            percentDoneText.visibility = if (!torrent.completed || torrent.rechecking) VISIBLE else GONE
            percentDoneText.text = when {
                torrent.rechecking -> context.getString(R.string.checking_progress_text, 100 * progress)
                torrent.completed -> ""
                else -> String.format(Locale.getDefault(), "%.2f%%", 100 * progress)
            }

            remainingTimeText.visibility = if (!torrent.completed && !torrent.rechecking) VISIBLE else GONE
            remainingTimeText.text = if (!torrent.completed && !torrent.rechecking) when {
                torrent.eta < 0 -> context.getString(R.string.eta_unknown)
                torrent.eta > ETA_INFINITE_THRESHOLD -> context.getString(R.string.eta_infinite)
                else -> context.getString(R.string.eta, displayableTime(torrent.eta))
            } else ""

            if (torrent.hasErrorOrWarning()) {
                errorText.visibility = VISIBLE
                errorText.text = torrent.errorMessage ?: context.getString(R.string.unknown_error_message)
                @DrawableRes val iconRes = torrent.errorIcon()
                if (iconRes == 0) {
                    errorText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                } else {
                    val size = context.resources.getDimensionPixelSize(R.dimen.torrent_list_error_icon_size)
                    val color = ColorUtils.resolveColor(context, android.R.attr.textColorSecondary, R.color.text_secondary)
                    val icon = ContextCompat.getDrawable(context, torrent.errorIcon())
                    icon?.setBounds(0, 0, size, size)
                    icon?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    errorText.setCompoundDrawables(icon, null, null, null)
                }
            } else {
                errorText.visibility = GONE
            }

            pauseButton.isPaused = torrent.paused

            selectionOverlay.visibility = if (torrent.selected) VISIBLE else GONE
        }

        @OnClick(R.id.pause_resume_button)
        fun onPauseResumeClicked() {
            pauseButton.toggle()

            if (pauseButton.isPaused) {
                listener.onPauseClicked(torrent.id)
            } else {
                listener.onResumeClicked(torrent.id)
            }
        }

        @OnClick(R.id.root_layout)
        fun onClicked() {
            return listener.onTorrentClicked(torrent.id)
        }

        @OnLongClick(R.id.root_layout)
        fun onLongClicked(): Boolean {
            return listener.onTorrentLongClicked(torrent.id)
        }
    }

    interface ClickListener {

        fun onPauseClicked(torrentId: Int)

        fun onResumeClicked(torrentId: Int)

        fun onTorrentClicked(torrentId: Int)

        fun onTorrentLongClicked(torrentId: Int): Boolean
    }
}
