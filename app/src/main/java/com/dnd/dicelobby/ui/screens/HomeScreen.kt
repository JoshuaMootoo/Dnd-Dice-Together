package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.ui.theme.AccentCrimson
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.theme.DarkBackground
import com.dnd.dicelobby.ui.theme.playerColors
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Entry screen where the player sets their display name and colour,
 * then chooses to create or join a lobby.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onCreateLobby: () -> Unit,
    onJoinLobby: () -> Unit
) {
    val playerName  by homeViewModel.playerName.collectAsStateWithLifecycle()
    val playerColor by homeViewModel.playerColor.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { homeViewModel.ensurePlayerId() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0B14), Color(0xFF1A0A1E), Color(0xFF0D0B14))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // App logo / title area
            Icon(
                Icons.Default.Casino,
                contentDescription = null,
                tint   = AccentGold,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = "DnD Dice Lobby",
                fontSize   = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = AccentGold,
                textAlign  = TextAlign.Center
            )
            Text(
                text  = "Roll together, anywhere",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(8.dp))

            // Player name input
            OutlinedTextField(
                value         = playerName,
                onValueChange = homeViewModel::updateName,
                label         = { Text("Your Name") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentGold,
                    focusedLabelColor    = AccentGold,
                    cursorColor          = AccentGold,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    unfocusedLabelColor  = Color.White.copy(alpha = 0.5f),
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White
                )
            )

            // Colour picker
            Text(
                text  = "Dice colour",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            ColorPicker(
                selectedColor = playerColor,
                onColorSelected = homeViewModel::updateColor
            )

            Spacer(Modifier.height(8.dp))

            // Primary actions
            Button(
                onClick  = onCreateLobby,
                enabled  = playerName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                shape  = RoundedCornerShape(14.dp)
            ) {
                Text("Create Game (DM)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            OutlinedButton(
                onClick  = onJoinLobby,
                enabled  = playerName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = OutlinedButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentGold)
            ) {
                Text("Join Game", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ColorPicker(selectedColor: String, onColorSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        playerColors.forEach { hex ->
            val isSelected = hex == selectedColor
            val col = try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (e: Exception) { Color.Red }

            Surface(
                onClick     = { onColorSelected(hex) },
                shape       = androidx.compose.foundation.shape.CircleShape,
                color       = col,
                border      = if (isSelected) androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                              else null,
                modifier    = Modifier.size(32.dp)
            ) {}
        }
    }
}
