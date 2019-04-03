package net.yupol.transmissionremote.app.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.yupol.transmissionremote.app.R

enum class PriorityViewModel(@StringRes val nameRes: Int, @DrawableRes val iconRes: Int) {

    LOW(R.string.priority_low, R.drawable.ic_priority_low),
    NORMAL(R.string.priority_normal, R.drawable.ic_priority_normal),
    HIGH(R.string.priority_high, R.drawable.ic_priority_high);
}
