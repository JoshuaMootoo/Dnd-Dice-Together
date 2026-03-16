package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.models.Player
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel
import com.dnd.dicelobby.ui.viewmodels.LobbyViewModel

/**
 * Screen shown to the player who is creating (hosting) a new lobby.
 * Displays the host IP so other players can join manually.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLobbyScreen(
    homeViewModel: HomeViewModel,
    lobbyViewModel: LobbyViewModel,
    onLobbyCreated: () -> Unit,
    onBack: () -> Unit
) {
    val playerName  by homeViewModel.playerName.collectAsStateWithLifecycle()
    val playerId    by homeViewModel.playerId.collectAsStateWithLifecycle()
    val playerColor by homeViewModel.playerColor.collectAsStateWithLifecycle()
    val hostIp      by lobbyViewModel.hostIp.collectAsStateWithLifecycle()

    val clipboard = LocalClipboardManager.current
    var lobbyStarted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Lobby") },
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
                text = "You will be the Dungeon Master",
                style = MaterialTheme.typography.titleLarge,
                color = AccentGold
            )

            Text(
                text  = "Creating a lobby starts a local server on your device.\n" +
                        "Share your IP address with other players so they can join.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!lobbyStarted) {
                Button(
                    onClick = {
                        val dmPlayer = Player(
                            id    = playerId,
                            name  = playerName,
                            isDM  = true,
                            color = playerColor
                        )
                        lobbyViewModel.startAsHost(dmPlayer)
                        lobbyStarted = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Start Lobby Server", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            } else {
                // Server is running — show IP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your IP Address", color = AccentGold, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text     = hostIp,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color    = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { clipboard.setText(AnnotatedString(hostIp)) }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null,
                                 modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy")
                        }
                    }
                }

                Text(
                    text  = "Port: 9876  •  Max players: 8",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )

                Button(
                    onClick  = onLobbyCreated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Enter Lobby", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
