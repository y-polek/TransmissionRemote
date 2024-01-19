package net.yupol.transmissionremote.app.preferences.server

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.theme.AppTheme

@Composable
fun ServersScene(
    viewModel: ServersListViewModel = viewModel(),
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.servers),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Go back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onAddServerClicked() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Localized description"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                startDestination = "servers"
            ) {
                composable(route = "servers") {
                    ServersList(
                        servers = uiState.servers,
                        selectedServerId = uiState.selectedServerId,
                        onServerClicked = { server ->
                            // TODO: move to ViewModel
                            navController.navigate("servers/${server.id}")
                            viewModel.onServerClicked(server)
                        },
                        onServerSelected = viewModel::onServerSelected
                    )
                }

                composable("servers/{server_id}") { backStackEntry ->
                    ServerScene(backStackEntry.arguments?.getString("server_id"))
                }
            }
        }
    }
}
