package net.yupol.transmissionremote.app.preferences.server

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.server.Server

@Composable
fun ServersList(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit
) {
    val viewModel: ServersListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val navigateToEvent by viewModel.navigateTo.collectAsState()
    navigateToEvent?.getContentIfNotHandled()?.let { destination ->
        navigateTo(destination)
    }

    Scaffold(
        modifier = modifier,
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
                    IconButton(onClick = viewModel::onBackClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onAddServerClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = stringResource(id = R.string.add_server)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(uiState.servers) { server ->
                ServerTile(
                    server = server,
                    isSelected = server.id == uiState.selectedServerId,
                    onServerClicked = viewModel::onServerClicked,
                    onServerSelected = viewModel::onServerSelected
                )
                Divider()
            }
        }
    }
}

@Composable
fun ServerTile(
    server: Server,
    isSelected: Boolean,
    onServerClicked: (Server) -> Unit,
    onServerSelected: (Server) -> Unit
) {
    Row(
        modifier = Modifier.clickable {
            onServerClicked(server)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = server.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "${server.host}:${server.port}",
                style = MaterialTheme.typography.subtitle1
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = {
                onServerSelected(server)
            }
        )
    }
}
