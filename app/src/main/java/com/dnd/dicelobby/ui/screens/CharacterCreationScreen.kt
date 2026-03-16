package com.dnd.dicelobby.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.models.DND_RACES
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.theme.AccentCrimson
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel

/**
 * Screen for picking a D&D 5e race and optional subrace.
 * Selections are persisted via [HomeViewModel] and shown on the Home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val selectedRace    by homeViewModel.playerRace.collectAsStateWithLifecycle()
    val selectedSubrace by homeViewModel.playerSubrace.collectAsStateWithLifecycle()

    val currentRaceData = DND_RACES.find { it.name == selectedRace }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Race", color = AccentGold, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AccentGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0B14))
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0D0B14), Color(0xFF1A0A1E), Color(0xFF0D0B14))
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section header
                Text(
                    text = "Select your race",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )

                // Race grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(DND_RACES) { race ->
                        val isSelected = race.name == selectedRace
                        OutlinedButton(
                            onClick = { homeViewModel.updateRace(race.name) },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.25f)
                                                else Color.Transparent,
                                contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.8f)
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AccentGold else Color.White.copy(alpha = 0.2f)
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text       = race.name,
                                fontSize   = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines   = 2
                            )
                        }
                    }
                }

                // Subrace section — only shown when the selected race has subraces
                AnimatedVisibility(
                    visible = currentRaceData != null && currentRaceData.subraces.isNotEmpty(),
                    enter   = expandVertically(),
                    exit    = shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select subrace",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )

                        currentRaceData?.subraces?.forEach { subrace ->
                            val isSelected = subrace == selectedSubrace
                            OutlinedButton(
                                onClick = { homeViewModel.updateSubrace(subrace) },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.25f)
                                                    else Color.Transparent,
                                    contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.8f)
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AccentGold else Color.White.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text       = subrace,
                                    fontSize   = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Current selection summary
                if (selectedRace.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1E1A2E)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text       = "Selected",
                                color      = AccentGold,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text  = buildString {
                                    append(selectedRace)
                                    if (selectedSubrace.isNotBlank()) append(" · $selectedSubrace")
                                },
                                color      = Color.White,
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Confirm button
                Button(
                    onClick  = onBack,
                    enabled  = selectedRace.isNotBlank() &&
                               (currentRaceData?.subraces.isNullOrEmpty() || selectedSubrace.isNotBlank()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCrimson),
                    shape  = RoundedCornerShape(14.dp)
                ) {
                    Text("Confirm Race", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
