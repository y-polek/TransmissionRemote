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
import androidx.hilt.navigation.compose.hiltViewModel
import net.yupol.transmissionremote.app.R

@Composable
fun ServerScene(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit
) {
    val viewModel: ServerViewModel = hiltViewModel()
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
                    IconButton(onClick = viewModel::onRemoveServerClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(id = R.string.remove_server)
                        )
                    }
                    IconButton(onClick = viewModel::onSaveClicked) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = stringResource(id = R.string.save_server)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Text(
            modifier = Modifier.padding(paddingValues),
            text = "Server: ${uiState.serverName}"
        )
    }
}
