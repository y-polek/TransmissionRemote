package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.pageobjects.AddServerScreen.Companion.addServerScreen
import net.yupol.transmissionremote.app.e2e.pageobjects.WelcomeScreen.Companion.welcomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddServerTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun add_server() {
        welcomeScreen {
            assertAddServerButtonDisplayed()
            clickAddServerButton()
        }

        addServerScreen {
            enterServerName("Test server")
            enterHostName("192.168.1.1")
            clickOk()
        }
    }
}
