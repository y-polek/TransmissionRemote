package net.yupol.transmissionremote.app.navigation

import androidx.navigation.NavController
import net.yupol.transmissionremote.app.navigation.NavigationEvent.NavigateToRoute
import net.yupol.transmissionremote.app.navigation.NavigationEvent.NavigateUp

fun NavController.navigate(event: NavigationEvent) {
    when (event) {
        is NavigateUp -> {
            navigateUp()
        }
        is NavigateToRoute -> {
            navigate(route = event.route)
        }
    }
}
