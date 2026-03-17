package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.models.DND_CLASSES
import com.dnd.dicelobby.models.DND_RACES
import com.dnd.dicelobby.ui.theme.AccentCrimson
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel
import kotlin.math.floor

private val SHEET_BG   = listOf(Color(0xFF0D0B14), Color(0xFF1A0A1E), Color(0xFF0D0B14))
private val CARD_COLOR = Color(0xFF1E1A2E)
private val FIELD_COLOR= Color(0xFF16121F)

val DND_BACKGROUNDS = listOf(
    "Acolyte", "Anthropologist", "Archaeologist", "Charlatan", "City Watch",
    "Clan Crafter", "Cloistered Scholar", "Courtier", "Criminal", "Entertainer",
    "Faction Agent", "Far Traveler", "Folk Hero", "Guild Artisan", "Haunted One",
    "Hermit", "Inheritor", "Mercenary Veteran", "Noble", "Outlander",
    "Sage", "Sailor", "Soldier", "Spy", "Urchin"
)

val DND_ALIGNMENTS = listOf(
    "Lawful Good", "Neutral Good", "Chaotic Good",
    "Lawful Neutral", "True Neutral", "Chaotic Neutral",
    "Lawful Evil", "Neutral Evil", "Chaotic Evil"
)

/**
 * Full character sheet screen: Basic Information + Ability Scores sections.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val characterName by homeViewModel.characterName.collectAsStateWithLifecycle()
    val playerName    by homeViewModel.playerName.collectAsStateWithLifecycle()
    val race          by homeViewModel.playerRace.collectAsStateWithLifecycle()
    val subrace       by homeViewModel.playerSubrace.collectAsStateWithLifecycle()
    val cls           by homeViewModel.playerClass.collectAsStateWithLifecycle()
    val background    by homeViewModel.background.collectAsStateWithLifecycle()
    val alignment     by homeViewModel.alignment.collectAsStateWithLifecycle()
    val level         by homeViewModel.level.collectAsStateWithLifecycle()
    val xp            by homeViewModel.xp.collectAsStateWithLifecycle()
    val str           by homeViewModel.strScore.collectAsStateWithLifecycle()
    val dex           by homeViewModel.dexScore.collectAsStateWithLifecycle()
    val con           by homeViewModel.conScore.collectAsStateWithLifecycle()
    val int_          by homeViewModel.intScore.collectAsStateWithLifecycle()
    val wis           by homeViewModel.wisScore.collectAsStateWithLifecycle()
    val cha           by homeViewModel.chaScore.collectAsStateWithLifecycle()

    var basicExpanded  by remember { mutableStateOf(true) }
    var scoresExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (characterName.isNotBlank()) characterName else "Character Sheet",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AccentGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D0B14))
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(SHEET_BG))
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Basic Information ────────────────────────────────────────
                item {
                    CollapsibleSection(
                        title    = "Basic Information",
                        expanded = basicExpanded,
                        onToggle = { basicExpanded = !basicExpanded }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                            SheetTextField(
                                label = "Character Name",
                                value = characterName,
                                onValueChange = homeViewModel::updateCharacterName
                            )

                            SheetTextField(
                                label = "Player Name",
                                value = playerName,
                                onValueChange = homeViewModel::updateName
                            )

                            // Race dropdown
                            SheetDropdown(
                                label    = "Race",
                                selected = race,
                                options  = DND_RACES.map { it.name },
                                onSelect = homeViewModel::updateRace
                            )

                            // Subrace dropdown — only shown when race has subraces
                            val raceData = DND_RACES.find { it.name == race }
                            if (raceData != null && raceData.subraces.isNotEmpty()) {
                                SheetDropdown(
                                    label    = "Subrace",
                                    selected = subrace,
                                    options  = raceData.subraces.map { it.name },
                                    onSelect = homeViewModel::updateSubrace
                                )
                            }

                            SheetDropdown(
                                label    = "Class",
                                selected = cls,
                                options  = DND_CLASSES.map { it.name },
                                onSelect = homeViewModel::updateClass
                            )

                            SheetDropdown(
                                label    = "Background",
                                selected = background,
                                options  = DND_BACKGROUNDS,
                                onSelect = homeViewModel::updateBackground
                            )

                            SheetDropdown(
                                label    = "Alignment",
                                selected = alignment,
                                options  = DND_ALIGNMENTS,
                                onSelect = homeViewModel::updateAlignment
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SheetNumberField(
                                        label = "Level",
                                        value = level,
                                        onValueChange = homeViewModel::updateLevel,
                                        min = 1, max = 20
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SheetNumberField(
                                        label = "XP",
                                        value = xp,
                                        onValueChange = homeViewModel::updateXp,
                                        min = 0, max = 355000
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Ability Scores ───────────────────────────────────────────
                item {
                    CollapsibleSection(
                        title    = "Ability Scores",
                        expanded = scoresExpanded,
                        onToggle = { scoresExpanded = !scoresExpanded }
                    ) {
                        val scores = listOf(
                            "STR" to str, "DEX" to dex, "CON" to con,
                            "INT" to int_, "WIS" to wis, "CHA" to cha
                        )
                        // 3-column grid using chunked rows
                        scores.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { (stat, score) ->
                                    AbilityScoreCard(
                                        modifier  = Modifier.weight(1f),
                                        stat      = stat,
                                        score     = score,
                                        onChange  = { homeViewModel.updateAbilityScore(stat, it) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Collapsible section card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CollapsibleSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors   = CardDefaults.cardColors(containerColor = CARD_COLOR),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = AccentCrimson, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                IconButton(onClick = onToggle, modifier = Modifier.size(28.dp)) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = AccentCrimson
                    )
                }
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sheet input components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SheetTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 12.sp) },
        singleLine    = true,
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(8.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = AccentCrimson,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor    = AccentCrimson,
            unfocusedLabelColor  = Color.White.copy(alpha = 0.5f),
            focusedTextColor     = Color.White,
            unfocusedTextColor   = Color.White,
            cursorColor          = AccentCrimson,
            unfocusedContainerColor = FIELD_COLOR,
            focusedContainerColor   = FIELD_COLOR
        )
    )
}

@Composable
private fun SheetNumberField(label: String, value: Int, onValueChange: (Int) -> Unit, min: Int, max: Int) {
    OutlinedTextField(
        value         = value.toString(),
        onValueChange = { it.toIntOrNull()?.let { n -> onValueChange(n.coerceIn(min, max)) } },
        label         = { Text(label, fontSize = 12.sp) },
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(8.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = AccentCrimson,
            unfocusedBorderColor    = Color.White.copy(alpha = 0.2f),
            focusedLabelColor       = AccentCrimson,
            unfocusedLabelColor     = Color.White.copy(alpha = 0.5f),
            focusedTextColor        = Color.White,
            unfocusedTextColor      = Color.White,
            cursorColor             = AccentCrimson,
            unfocusedContainerColor = FIELD_COLOR,
            focusedContainerColor   = FIELD_COLOR
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier         = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value         = selected.ifBlank { "" },
            onValueChange = {},
            readOnly      = true,
            label         = { Text(label, fontSize = 12.sp) },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier      = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape         = RoundedCornerShape(8.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = AccentCrimson,
                unfocusedBorderColor    = Color.White.copy(alpha = 0.2f),
                focusedLabelColor       = AccentCrimson,
                unfocusedLabelColor     = Color.White.copy(alpha = 0.5f),
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
                unfocusedContainerColor = FIELD_COLOR,
                focusedContainerColor   = FIELD_COLOR,
                unfocusedTrailingIconColor = Color.White.copy(alpha = 0.5f),
                focusedTrailingIconColor   = AccentCrimson
            )
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(Color(0xFF1E1A2E))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(option, color = if (option == selected) AccentGold else Color.White, fontSize = 14.sp) },
                    onClick = { onSelect(option); expanded = false },
                    modifier = Modifier.background(
                        if (option == selected) AccentCrimson.copy(alpha = 0.15f) else Color.Transparent
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Ability score card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AbilityScoreCard(
    modifier: Modifier = Modifier,
    stat: String,
    score: Int,
    onChange: (Int) -> Unit
) {
    val modifier_ = (score - 10) / 2   // floor division
    val modText   = if (modifier_ >= 0) "+$modifier_" else "$modifier_"
    var textVal   by remember(score) { mutableStateOf(score.toString()) }

    Card(
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF13101C)),
        shape    = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stat label
            Text(
                text       = stat,
                color      = AccentCrimson,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Modifier circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(2.dp, AccentCrimson, CircleShape)
                    .background(Color(0xFF1A0A1E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = modText,
                    color      = Color.White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                )
            }

            // Score input box
            OutlinedTextField(
                value         = textVal,
                onValueChange = { v ->
                    textVal = v
                    v.toIntOrNull()?.let { onChange(it) }
                },
                singleLine    = true,
                textStyle     = androidx.compose.ui.text.TextStyle(
                    color      = Color.White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape           = RoundedCornerShape(8.dp),
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = AccentCrimson,
                    unfocusedBorderColor    = Color.White.copy(alpha = 0.25f),
                    focusedContainerColor   = Color(0xFF16121F),
                    unfocusedContainerColor = Color(0xFF16121F),
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    cursorColor             = AccentCrimson
                )
            )
        }
    }
}
