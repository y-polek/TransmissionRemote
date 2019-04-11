package net.yupol.transmissionremote.app.utils

import timber.log.Timber

inline fun <R> printTime(blockName: String = "", block: () -> R): R {
    val start = System.currentTimeMillis()
    val result = block.invoke()
    val time = System.currentTimeMillis() - start

    Timber.i("$blockName time(ms): $time")

    return result
}
