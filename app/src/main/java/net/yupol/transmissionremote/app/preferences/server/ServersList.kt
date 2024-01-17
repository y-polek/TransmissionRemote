package net.yupol.transmissionremote.app.preferences.server

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.yupol.transmissionremote.app.server.Server

@Composable
fun ServersList(
    modifier: Modifier = Modifier,
    servers: List<Server>,
    selectedServerId: String?,
    onServerClicked: (Server) -> Unit,
    onServerSelected: (Server) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(servers) { server ->
            ServerTile(
                server = server,
                isSelected = server.id == selectedServerId,
                onServerClicked = onServerClicked,
                onServerSelected = onServerSelected
            )
            Divider()
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
            Text(text = server.name, style = MaterialTheme.typography.h6)
            Text(text = "${server.host}:${server.port}", style = MaterialTheme.typography.subtitle2)
        }
        RadioButton(
            selected = isSelected,
            onClick = {
                onServerSelected(server)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServersListPreview() {
    val server1 = Server("NAS", "192.168.1.10", 9091)
    val server2 = Server("Laptop", "192.168.1.20", 9091)
    MaterialTheme {
        ServersList(
            modifier = Modifier.fillMaxSize(),
            servers = listOf(server1, server2),
            selectedServerId =server2.id,
            onServerClicked = {},
            onServerSelected = {}
        )
    }
}
