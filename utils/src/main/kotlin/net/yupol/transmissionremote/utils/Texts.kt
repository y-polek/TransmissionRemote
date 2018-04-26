@file:JvmName("Texts")

package net.yupol.transmissionremote.utils

import com.vdurmont.emoji.EmojiManager

/**
 * Returns first symbol in a string.
 * Supports emojis and unicode code points.
 */
fun String.firstSymbol(): String {
    if (isEmpty()) return ""

    for (i in length downTo 1) {
        val str = substring(0, i)
        if (EmojiManager.isEmoji(str)) return str
    }

    return buildString { appendCodePoint(this@firstSymbol.codePointAt(0)) }
}
