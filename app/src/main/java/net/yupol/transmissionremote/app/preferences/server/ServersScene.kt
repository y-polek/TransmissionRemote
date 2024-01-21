package net.yupol.transmissionremote.app.preferences.server

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.yupol.transmissionremote.app.navigation.NavigationEvent
import net.yupol.transmissionremote.app.navigation.navigate
import net.yupol.transmissionremote.app.theme.AppTheme

@Composable
fun ServersScene(onFinish: () -> Unit) {
    val navController = rememberNavController()

    AppTheme {
        NavHost(
            navController = navController,
            startDestination = "servers"
        ) {
            composable(route = "servers") {
                ServersList { navigationEvent ->
                    if (navigationEvent is NavigationEvent.NavigateUp) {
                        onFinish()
                    } else {
                        navController.navigate(navigationEvent)
                    }
                }
            }

            composable(
                route = "servers/server?server_id={server_id}",
                arguments = listOf(
                    navArgument("server_id") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) {
                ServerScene { navigationEvent ->
                    navController.navigate(navigationEvent)
                }
            }
        }
    }
}
