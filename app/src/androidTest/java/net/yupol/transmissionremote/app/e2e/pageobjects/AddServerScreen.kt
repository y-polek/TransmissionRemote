package net.yupol.transmissionremote.app.e2e.pageobjects

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.clickId
import net.yupol.transmissionremote.app.e2e.utils.inputText

class AddServerScreen {

    fun enterServerName(name: String) {
        inputText(R.id.server_name_edit_text, name)
    }

    fun enterHostName(name: String) {
        inputText(R.id.host_edit_text, name)
    }

    fun clickOk() {
        clickId(R.id.ok_button)
    }

    companion object {
        fun addServerScreen(init: AddServerScreen.() -> Unit) {
            AddServerScreen().init()
        }
    }
}
