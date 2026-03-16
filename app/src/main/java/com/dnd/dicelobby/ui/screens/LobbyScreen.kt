package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.ui.components.PlayerList
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.viewmodels.LobbyViewModel

/**
 * Lobby waiting room.
 * Shows connected players and a "Start Rolling" button (navigates to the Dice screen).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    lobbyViewModel: LobbyViewModel,
    onStartRolling: () -> Unit,
    onLeave: () -> Unit
) {
    val players     by lobbyViewModel.players.collectAsStateWithLifecycle()
    val localPlayer by lobbyViewModel.localPlayer.collectAsStateWithLifecycle()
    val message     by lobbyViewModel.message.collectAsStateWithLifecycle()
    val isHost      by lobbyViewModel.isHost.collectAsStateWithLifecycle()
    val hostIp      by lobbyViewModel.hostIp.collectAsStateWithLifecycle()
    val connected   by lobbyViewModel.connected.collectAsStateWithLifecycle()

    // Show snackbar for transient messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
            lobbyViewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Lobby", fontWeight = FontWeight.Bold)
                        if (isHost && hostIp.isNotEmpty()) {
                            Text(
                                text  = "Host IP: $hostIp",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLeave) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Leave lobby")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Players header
                Text(
                    text     = "Players (${players.size}/8)",
                    style    = MaterialTheme.typography.titleMedium,
                    color    = AccentGold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
                PlayerList(
                    players       = players,
                    localPlayerId = localPlayer?.id ?: "",
                    modifier      = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))

                // Connection info card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text       = if (isHost) "You are the Dungeon Master" else "Connected as Player",
                            fontWeight = FontWeight.SemiBold,
                            color      = if (isHost) AccentGold else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = if (isHost)
                                "Share your IP address with players so they can join."
                            else
                                "Waiting for the DM to open the dice screen…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        if (!connected && !isHost) {
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color    = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text  = "Reconnecting…",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Bottom action bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick  = onStartRolling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Start Rolling", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick  = onLeave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Leave Lobby")
                }
            }
        }
    }
}
