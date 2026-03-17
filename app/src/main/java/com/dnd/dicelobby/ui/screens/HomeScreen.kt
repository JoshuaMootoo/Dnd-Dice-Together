package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.ui.theme.*
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Entry screen — Baldur's Gate 3 styled.
 * Dark, warm, ornate gold accents with gothic serif title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onChooseRace: () -> Unit,
    onCreateLobby: () -> Unit,
    onJoinLobby: () -> Unit
) {
    val playerName     by homeViewModel.playerName.collectAsStateWithLifecycle()
    val playerColor    by homeViewModel.playerColor.collectAsStateWithLifecycle()
    val playerRace     by homeViewModel.playerRace.collectAsStateWithLifecycle()
    val playerSubrace  by homeViewModel.playerSubrace.collectAsStateWithLifecycle()
    val playerClass    by homeViewModel.playerClass.collectAsStateWithLifecycle()
    val playerSubclass by homeViewModel.playerSubclass.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { homeViewModel.ensurePlayerId() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BG3Background, Color(0xFF120E06), BG3Background)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // ── Title block ────────────────────────────────────────────────
            Icon(
                Icons.Default.Casino,
                contentDescription = null,
                tint     = BG3Gold,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text      = "DnD Dice Lobby",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize  = 28.sp,
                letterSpacing = 3.sp,
                color     = BG3Gold,
                textAlign = TextAlign.Center
            )

            // Ornate subtitle divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    color     = BG3GoldBorder,
                    thickness = 1.dp
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(5.dp)
                        .graphicsLayer { rotationZ = 45f }
                        .background(BG3GoldBorder)
                )
                HorizontalDivider(
                    modifier  = Modifier.weight(1f),
                    color     = BG3GoldBorder,
                    thickness = 1.dp
                )
            }

            Text(
                text      = "Gather your party. Roll together.",
                color     = BG3CreamMuted,
                fontSize  = 13.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            // ── Player name ────────────────────────────────────────────────
            OutlinedTextField(
                value         = playerName,
                onValueChange = homeViewModel::updateName,
                label         = { Text("YOUR NAME", fontSize = 11.sp, letterSpacing = 1.sp) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = BG3Gold,
                    focusedLabelColor    = BG3Gold,
                    cursorColor          = BG3Gold,
                    unfocusedBorderColor = BG3GoldBorder,
                    unfocusedLabelColor  = BG3CreamMuted,
                    focusedTextColor     = BG3Cream,
                    unfocusedTextColor   = BG3Cream
                )
            )

            // ── Colour picker ──────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text      = "DICE COLOUR",
                    color     = BG3CreamMuted,
                    fontSize  = 11.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium,
                    modifier  = Modifier.padding(bottom = 8.dp)
                )
                ColorPicker(
                    selectedColor   = playerColor,
                    onColorSelected = homeViewModel::updateColor
                )
            }

            // ── Character summary ──────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(BG3SurfaceCard)
                    .border(1.dp, BG3GoldBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text      = "CHARACTER",
                        color     = BG3CreamMuted,
                        fontSize  = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    if (playerRace.isNotBlank() || playerClass.isNotBlank()) {
                        if (playerRace.isNotBlank()) {
                            Text(
                                text = buildString {
                                    append(playerRace)
                                    if (playerSubrace.isNotBlank()) append("  ·  $playerSubrace")
                                },
                                color      = BG3Gold,
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (playerClass.isNotBlank()) {
                            Text(
                                text = buildString {
                                    append(playerClass)
                                    if (playerSubclass.isNotBlank()) append("  ·  $playerSubclass")
                                },
                                color    = BG3CreamMuted,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text     = "Not configured",
                            color    = BG3GoldBorder,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }

                OutlinedButton(
                    onClick = onChooseRace,
                    shape   = RoundedCornerShape(4.dp),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = BG3Gold),
                    border  = androidx.compose.foundation.BorderStroke(1.dp, BG3GoldBorder)
                ) {
                    Text(
                        text = if (playerRace.isBlank() && playerClass.isBlank()) "SET UP" else "EDIT",
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Primary actions ────────────────────────────────────────────
            // DM: dark red button with gold border
            Button(
                onClick  = onCreateLobby,
                enabled  = playerName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(
                        1.5.dp,
                        if (playerName.isNotBlank()) BG3RedBright else BG3GoldBorder.copy(alpha = 0.3f),
                        RoundedCornerShape(4.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = BG3Red,
                    contentColor           = BG3Cream,
                    disabledContainerColor = BG3Red.copy(alpha = 0.3f),
                    disabledContentColor   = BG3CreamMuted.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "DUNGEON MASTER  —  CREATE GAME",
                    fontWeight    = FontWeight.Bold,
                    fontSize      = 14.sp,
                    letterSpacing = 1.sp,
                    textAlign     = TextAlign.Center
                )
            }

            // Player: outlined gold button on dark
            OutlinedButton(
                onClick  = onJoinLobby,
                enabled  = playerName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor         = BG3Gold,
                    disabledContentColor = BG3GoldBorder.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (playerName.isNotBlank()) BG3Gold else BG3GoldBorder.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    "JOIN PARTY",
                    fontWeight    = FontWeight.Bold,
                    fontSize      = 16.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun ColorPicker(selectedColor: String, onColorSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        playerColors.forEach { hex ->
            val isSelected = hex == selectedColor
            val col = try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (e: Exception) { Color.Red }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(col)
                    .then(
                        if (isSelected)
                            Modifier.border(2.5.dp, BG3Gold, CircleShape)
                        else
                            Modifier.border(1.dp, BG3GoldBorder.copy(alpha = 0.4f), CircleShape)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = { onColorSelected(hex) },
                    shape   = CircleShape,
                    color   = Color.Transparent,
                    modifier = Modifier.fillMaxSize()
                ) {}
            }
        }
    }
}
