package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.models.Player
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel
import com.dnd.dicelobby.ui.viewmodels.LobbyViewModel

/**
 * Screen for a non-DM player to join an existing lobby by entering the host IP.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinLobbyScreen(
    homeViewModel: HomeViewModel,
    lobbyViewModel: LobbyViewModel,
    onJoined: () -> Unit,
    onBack: () -> Unit
) {
    val playerName  by homeViewModel.playerName.collectAsStateWithLifecycle()
    val playerId    by homeViewModel.playerId.collectAsStateWithLifecycle()
    val playerColor by homeViewModel.playerColor.collectAsStateWithLifecycle()
    val connected   by lobbyViewModel.connected.collectAsStateWithLifecycle()
    val message     by lobbyViewModel.message.collectAsStateWithLifecycle()

    var hostIp      by remember { mutableStateOf("192.168.1.") }
    var isConnecting by remember { mutableStateOf(false) }

    // Navigate once connected
    LaunchedEffect(connected) {
        if (connected && isConnecting) onJoined()
    }

    LaunchedEffect(message) {
        if (message.isNotEmpty()) isConnecting = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Lobby") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text  = "Enter the DM's IP address to connect to their lobby.",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value         = hostIp,
                onValueChange = { hostIp = it },
                label         = { Text("Host IP Address") },
                placeholder   = { Text("e.g. 192.168.1.42") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction    = ImeAction.Done
                )
            )

            Text(
                text  = "Port 9876 is used automatically.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 13.sp
            )

            if (message.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text  = message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    isConnecting = true
                    lobbyViewModel.clearMessage()
                    val player = Player(
                        id    = playerId,
                        name  = playerName,
                        isDM  = false,
                        color = playerColor
                    )
                    lobbyViewModel.joinAsClient(player, hostIp.trim())
                },
                enabled  = hostIp.isNotBlank() && !isConnecting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Connecting…")
                } else {
                    Text("Connect", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
