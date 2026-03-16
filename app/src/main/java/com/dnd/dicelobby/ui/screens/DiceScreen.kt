package com.dnd.dicelobby.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.ui.components.DiceAnimationOverlay
import com.dnd.dicelobby.ui.components.DiceSelector
import com.dnd.dicelobby.ui.components.PlayerList
import com.dnd.dicelobby.ui.components.RollLogList
import com.dnd.dicelobby.ui.theme.AccentCrimson
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.viewmodels.DiceViewModel
import com.dnd.dicelobby.ui.viewmodels.LobbyViewModel

/**
 * Main game screen — layout:
 *  ┌─────────────────────┐
 *  │   Players strip     │  (PlayerList)
 *  ├─────────────────────┤
 *  │   Roll log          │  (RollLogList — takes most of the space)
 *  ├─────────────────────┤
 *  │   Dice controls     │  (DiceSelector + count + modifier + Roll button)
 *  └─────────────────────┘
 *
 * When [DiceViewModel.showAnimation] is true, [DiceAnimationOverlay] covers the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(
    lobbyViewModel: LobbyViewModel,
    diceViewModel: DiceViewModel,
    onLeave: () -> Unit
) {
    val players       by lobbyViewModel.players.collectAsStateWithLifecycle()
    val rolls         by lobbyViewModel.rolls.collectAsStateWithLifecycle()
    val localPlayer   by lobbyViewModel.localPlayer.collectAsStateWithLifecycle()
    val message       by lobbyViewModel.message.collectAsStateWithLifecycle()
    val isHost        by lobbyViewModel.isHost.collectAsStateWithLifecycle()

    val selectedDice  by diceViewModel.selectedDice.collectAsStateWithLifecycle()
    val diceCount     by diceViewModel.diceCount.collectAsStateWithLifecycle()
    val modifier      by diceViewModel.modifier.collectAsStateWithLifecycle()
    val showAnimation by diceViewModel.showAnimation.collectAsStateWithLifecycle()
    val hiddenRoll    by diceViewModel.hiddenRoll.collectAsStateWithLifecycle()
    val customFormula by diceViewModel.customFormula.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showClearDialog   by remember { mutableStateOf(false) }
    var modifierText      by remember { mutableStateOf("") }

    // Wire physics settled callback → lobby roll request
    LaunchedEffect(Unit) {
        diceViewModel.onRollReady = { formula, hidden ->
            lobbyViewModel.requestRoll(formula, hidden)
        }
    }

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
                title = { Text("Roll Dice", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear history")
                    }
                    IconButton(onClick = onLeave) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Leave")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── Players strip ──────────────────────────────────────────
                PlayerList(
                    players       = players,
                    localPlayerId = localPlayer?.id ?: "",
                    modifier      = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // ── Roll log ────────────────────────────────────────────────
                RollLogList(
                    rolls       = rolls,
                    viewerIsDM  = isHost,
                    onReveal    = lobbyViewModel::revealHiddenRoll,
                    modifier    = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                HorizontalDivider()

                // ── Dice controls ───────────────────────────────────────────
                DiceControls(
                    selectedDice  = selectedDice,
                    diceCount     = diceCount,
                    modifierText  = modifierText,
                    customFormula = customFormula,
                    hiddenRoll    = hiddenRoll,
                    isHost        = isHost,
                    onDiceSelect  = diceViewModel::selectDice,
                    onCountChange = { diceViewModel.setDiceCount(it) },
                    onModChange   = { text ->
                        modifierText = text
                        diceViewModel.setModifier(text.toIntOrNull() ?: 0)
                    },
                    onFormulaChange = diceViewModel::setCustomFormula,
                    onHiddenToggle  = diceViewModel::setHiddenRoll,
                    onRoll = {
                        diceViewModel.startRoll(diceCount)
                    }
                )
            }

            // ── Animation overlay (on top of everything) ──────────────────
            DiceAnimationOverlay(
                visible      = showAnimation,
                physicsWorld = diceViewModel.physicsWorld,
                diceType     = selectedDice,
                playerColor  = localPlayer?.color ?: "#E53935",
                onSettled    = diceViewModel::onPhysicsSettled
            )
        }
    }

    // Clear history confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title  = { Text("Clear Roll History?") },
            text   = { Text("This will remove all roll records from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    lobbyViewModel.clearHistory()
                    showClearDialog = false
                }) { Text("Clear", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiceControls(
    selectedDice: DiceType,
    diceCount: Int,
    modifierText: String,
    customFormula: String,
    hiddenRoll: Boolean,
    isHost: Boolean,
    onDiceSelect: (DiceType) -> Unit,
    onCountChange: (Int) -> Unit,
    onModChange: (String) -> Unit,
    onFormulaChange: (String) -> Unit,
    onHiddenToggle: (Boolean) -> Unit,
    onRoll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick die picker
        DiceSelector(
            selectedType   = selectedDice,
            onTypeSelected = onDiceSelect,
            modifier       = Modifier.fillMaxWidth()
        )

        // Count + Modifier row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dice count stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.5f)
            ) {
                OutlinedButton(
                    onClick  = { onCountChange(diceCount - 1) },
                    enabled  = diceCount > 1,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("−") }

                Text(
                    text     = "${diceCount}",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                OutlinedButton(
                    onClick  = { onCountChange(diceCount + 1) },
                    enabled  = diceCount < 20,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("+") }

                Spacer(Modifier.width(4.dp))
                Text(selectedDice.label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            // Modifier input
            OutlinedTextField(
                value         = modifierText,
                onValueChange = onModChange,
                label         = { Text("±Mod") },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(0.5f)
            )
        }

        // Custom formula field (optional override)
        OutlinedTextField(
            value         = customFormula,
            onValueChange = onFormulaChange,
            label         = { Text("Custom formula (e.g. 4d6kh3)") },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth()
        )

        // Hidden roll toggle (DM only)
        if (isHost) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked         = hiddenRoll,
                    onCheckedChange = onHiddenToggle
                )
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Default.VisibilityOff, contentDescription = null,
                     tint = if (hiddenRoll) AccentCrimson else Color.Gray)
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = "Hidden Roll (DM only)",
                    color = if (hiddenRoll) AccentCrimson else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Roll button
        Button(
            onClick  = onRoll,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape  = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hiddenRoll && isHost) AccentCrimson
                                 else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Casino, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text       = if (hiddenRoll && isHost) "Roll (Hidden)" else "Roll!",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp
            )
        }
    }
}
