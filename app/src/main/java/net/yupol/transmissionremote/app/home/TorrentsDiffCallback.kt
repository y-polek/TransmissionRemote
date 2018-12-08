package net.yupol.transmissionremote.app.home

import android.support.v7.util.DiffUtil
import net.yupol.transmissionremote.app.model.TorrentViewModel

class TorrentsDiffCallback(
        private val newTorrents: List<TorrentViewModel>,
        private val oldTorrents: List<TorrentViewModel>): DiffUtil.Callback()
{
    override fun getOldListSize() = oldTorrents.size

    override fun getNewListSize() = newTorrents.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTorrents[oldItemPosition].id == newTorrents[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTorrents[oldItemPosition].equals(newTorrents[newItemPosition])
    }
}
