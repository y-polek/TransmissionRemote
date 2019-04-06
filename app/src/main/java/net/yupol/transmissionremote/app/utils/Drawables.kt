package net.yupol.transmissionremote.app.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.joinedDrawables(@DrawableRes vararg drawableRes: Int): Drawable {
    if (drawableRes.isEmpty()) return LayerDrawable(emptyArray())

    val drawables = drawableRes.map { ContextCompat.getDrawable(this, it) ?: throw RuntimeException("Unknown drawable ID: $it") }

    val width = drawables.sumBy { it.intrinsicWidth }

    val joinedDrawable = LayerDrawable(drawables.toTypedArray())

    val iconCount = drawableRes.size
    val iconWidth = width / iconCount
    var offset = 0
    drawables.forEachIndexed { index, drawable ->
        joinedDrawable.setLayerInset(index, index * iconWidth, 0, (iconCount - index - 1) * iconWidth, 0)
        offset += drawable.intrinsicWidth
    }

    return joinedDrawable
}
