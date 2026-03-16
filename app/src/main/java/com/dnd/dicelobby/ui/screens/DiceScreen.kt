package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.ui.components.DiceAnimationOverlay
import com.dnd.dicelobby.ui.components.DiceSelector
import com.dnd.dicelobby.ui.components.PlayerList
import com.dnd.dicelobby.ui.components.RollLogList
import com.dnd.dicelobby.ui.theme.*
import com.dnd.dicelobby.ui.viewmodels.DiceViewModel
import com.dnd.dicelobby.ui.viewmodels.LobbyViewModel

/**
 * Main game screen — Baldur's Gate 3 styled layout:
 *  ┌─────────────────────┐
 *  │   Header + Players  │
 *  ├── ◆ ─────────────── │  (ornate gold divider)
 *  │   Roll log          │  (combat log — takes most of the space)
 *  ├── ◆ ─────────────── │  (ornate gold divider)
 *  │   Dice controls     │  (panel with BG3 styling)
 *  └─────────────────────┘
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
        containerColor = BG3Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "DICE CHAMBER",
                        style = MaterialTheme.typography.titleLarge,
                        color = BG3Gold
                    )
                },
                actions = {
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Clear history",
                            tint = BG3CreamMuted
                        )
                    }
                    IconButton(onClick = onLeave) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Leave",
                            tint = BG3CreamMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BG3Surface,
                    titleContentColor = BG3Gold
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(BG3Background)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ── Players strip ──────────────────────────────────────────
                PlayerList(
                    players       = players,
                    localPlayerId = localPlayer?.id ?: "",
                    modifier      = Modifier
                        .fillMaxWidth()
                        .background(BG3Surface)
                )

                OrnateDivider()

                // ── Roll log ────────────────────────────────────────────────
                RollLogList(
                    rolls       = rolls,
                    viewerIsDM  = isHost,
                    onReveal    = lobbyViewModel::revealHiddenRoll,
                    modifier    = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                OrnateDivider()

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

            // ── Animation overlay ──────────────────────────────────────────
            DiceAnimationOverlay(
                visible      = showAnimation,
                physicsWorld = diceViewModel.physicsWorld,
                diceType     = selectedDice,
                playerColor  = localPlayer?.color ?: "#C8A84B",
                onSettled    = diceViewModel::onPhysicsSettled
            )
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor   = BG3SurfaceCard,
            title  = {
                Text(
                    "Clear Roll History?",
                    color = BG3Gold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text   = {
                Text(
                    "This will remove all roll records from this device.",
                    color = BG3CreamMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    lobbyViewModel.clearHistory()
                    showClearDialog = false
                }) {
                    Text("CLEAR", color = BG3RedBright, letterSpacing = 1.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("CANCEL", color = BG3CreamMuted, letterSpacing = 1.sp)
                }
            }
        )
    }
}

/** Gold ornate divider — a thin line with a diamond accent in the center. */
@Composable
private fun OrnateDivider(modifier: Modifier = Modifier) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            color     = BG3GoldBorder,
            thickness = 1.dp
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(7.dp)
                .graphicsLayer { rotationZ = 45f }
                .background(BG3GoldBorder)
        )
        HorizontalDivider(
            modifier  = Modifier.weight(1f),
            color     = BG3GoldBorder,
            thickness = 1.dp
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
            .background(BG3Surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Die picker
        DiceSelector(
            selectedType   = selectedDice,
            onTypeSelected = onDiceSelect,
            modifier       = Modifier.fillMaxWidth()
        )

        // Count + Modifier row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Dice count stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.weight(0.55f)
            ) {
                BG3IconButton(
                    onClick  = { onCountChange(diceCount - 1) },
                    enabled  = diceCount > 1,
                    label    = "−"
                )
                Text(
                    text       = "$diceCount",
                    modifier   = Modifier.padding(horizontal = 10.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    color      = BG3Cream
                )
                BG3IconButton(
                    onClick  = { onCountChange(diceCount + 1) },
                    enabled  = diceCount < 20,
                    label    = "+"
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = selectedDice.label.uppercase(),
                    color = BG3Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            // Modifier input
            OutlinedTextField(
                value         = modifierText,
                onValueChange = onModChange,
                label         = { Text("± MOD", fontSize = 11.sp, letterSpacing = 0.5.sp) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(0.45f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = BG3Gold,
                    focusedLabelColor    = BG3Gold,
                    cursorColor          = BG3Gold,
                    unfocusedBorderColor = BG3GoldBorder,
                    unfocusedLabelColor  = BG3CreamMuted,
                    focusedTextColor     = BG3Cream,
                    unfocusedTextColor   = BG3Cream
                ),
                shape = RoundedCornerShape(4.dp)
            )
        }

        // Custom formula
        OutlinedTextField(
            value         = customFormula,
            onValueChange = onFormulaChange,
            label         = { Text("Custom formula  e.g. 4d6kh3", fontSize = 11.sp) },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = BG3Gold,
                focusedLabelColor    = BG3Gold,
                cursorColor          = BG3Gold,
                unfocusedBorderColor = BG3GoldBorder,
                unfocusedLabelColor  = BG3CreamMuted,
                focusedTextColor     = BG3Cream,
                unfocusedTextColor   = BG3Cream
            ),
            shape = RoundedCornerShape(4.dp)
        )

        // Hidden roll (DM only)
        if (isHost) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked         = hiddenRoll,
                    onCheckedChange = onHiddenToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor  = BG3Cream,
                        checkedTrackColor  = BG3Red,
                        uncheckedThumbColor = BG3CreamMuted,
                        uncheckedTrackColor = BG3GoldBorder.copy(alpha = 0.3f)
                    )
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint     = if (hiddenRoll) BG3RedBright else BG3GoldBorder,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = "HIDDEN ROLL  (DM ONLY)",
                    color = if (hiddenRoll) BG3RedBright else BG3CreamMuted.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Roll button — BG3 style: dark fill, gold border, uppercase text
        val rollBtnBorder = if (hiddenRoll && isHost) BG3RedBright else BG3Gold
        val rollBtnBg     = if (hiddenRoll && isHost) BG3Red       else BG3SurfaceCard

        Button(
            onClick  = onRoll,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .border(1.5.dp, rollBtnBorder, RoundedCornerShape(4.dp)),
            shape  = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = rollBtnBg,
                contentColor   = rollBtnBorder
            )
        ) {
            Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text          = if (hiddenRoll && isHost) "ROLL  (HIDDEN)" else "ROLL THE DICE",
                fontWeight    = FontWeight.Bold,
                fontSize      = 16.sp,
                letterSpacing = 2.sp,
                textAlign     = TextAlign.Center
            )
        }
    }
}

/** Small square button matching BG3's minimal stepper style. */
@Composable
private fun BG3IconButton(onClick: () -> Unit, enabled: Boolean, label: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .border(1.dp, if (enabled) BG3GoldBorder else BG3GoldBorder.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
            .background(BG3SurfaceCard, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick  = onClick,
            enabled  = enabled,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            colors   = ButtonDefaults.textButtonColors(
                contentColor         = BG3Gold,
                disabledContentColor = BG3GoldBorder.copy(alpha = 0.3f)
            )
        ) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}
