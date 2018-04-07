@file:JvmName(name = "Torrents")

package net.yupol.transmissionremote.model

import net.yupol.transmissionremote.model.json.Torrent

fun Collection<Torrent>.ids() = map { it.id }.toIntArray()