package net.yupol.transmissionremote.app.res

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import net.yupol.transmissionremote.app.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorResources @Inject constructor(private val res: Resources) {

    @delegate:ColorInt
    val accent: Int by lazy {
        ResourcesCompat.getColor(res, R.color.accent, null)
    }
}
