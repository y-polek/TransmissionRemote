package net.yupol.transmissionremote.device.clipboard

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Clipboard @Inject constructor(context: Application) {

    private val manager: ClipboardManager by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    fun setPlainTextClip(label: CharSequence, text: CharSequence) {
        manager.primaryClip = ClipData.newPlainText(label, text)
    }
}
