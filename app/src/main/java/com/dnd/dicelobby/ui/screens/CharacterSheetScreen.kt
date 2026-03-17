package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

/** All 18 D&D 5e skills mapped to their governing ability. */
val DND_SKILLS: List<Pair<String, String>> = listOf(
    "Acrobatics"     to "DEX",
    "Animal Handling" to "WIS",
    "Arcana"         to "INT",
    "Athletics"      to "STR",
    "Deception"      to "CHA",
    "History"        to "INT",
    "Insight"        to "WIS",
    "Intimidation"   to "CHA",
    "Investigation"  to "INT",
    "Medicine"       to "WIS",
    "Nature"         to "INT",
    "Perception"     to "WIS",
    "Performance"    to "CHA",
    "Persuasion"     to "CHA",
    "Religion"       to "INT",
    "Sleight of Hand" to "DEX",
    "Stealth"        to "DEX",
    "Survival"       to "WIS"
)

/** Returns the proficiency bonus for a given character level (PHB table). */
fun proficiencyBonus(level: Int): Int = when {
    level <= 4  -> 2
    level <= 8  -> 3
    level <= 12 -> 4
    level <= 16 -> 5
    else        -> 6
}

/** Computes the total modifier for a skill check. */
fun skillModifier(abilityScore: Int, profBonus: Int, isProficient: Boolean, hasExpertise: Boolean): Int {
    val abilityMod = floor((abilityScore - 10) / 2.0).toInt()
    val profMult   = when {
        hasExpertise -> 2
        isProficient -> 1
        else         -> 0
    }
    return abilityMod + profBonus * profMult
}

/** Formats a modifier int as "+X" or "-X" or "0". */
fun fmtMod(mod: Int): String = if (mod >= 0) "+$mod" else "$mod"

/** Builds a 1d20 formula string from a modifier. */
fun skillRollFormula(modifier: Int): String = when {
    modifier > 0 -> "1d20+$modifier"
    modifier < 0 -> "1d20$modifier"
    else         -> "1d20"
}

/**
 * Full character sheet screen.
 *
 * @param onSkillRoll When non-null (e.g. accessed from the Dice screen), skill rows become
 *   tappable and invoke this callback with the roll modifier and a human-readable label.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    onSkillRoll: ((modifier: Int, label: String) -> Unit)? = null
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
    val skillProf     by homeViewModel.skillProficiencies.collectAsStateWithLifecycle()
    val skillExp      by homeViewModel.skillExpertise.collectAsStateWithLifecycle()

    var basicExpanded  by remember { mutableStateOf(true) }
    var scoresExpanded by remember { mutableStateOf(true) }
    var skillsExpanded by remember { mutableStateOf(true) }

    // Build an ability-score lookup used by the skills section
    val abilityScores = mapOf(
        "STR" to str, "DEX" to dex, "CON" to con,
        "INT" to int_, "WIS" to wis, "CHA" to cha
    )
    val profBonus = proficiencyBonus(level)

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

                            SheetDropdown(
                                label    = "Race",
                                selected = race,
                                options  = DND_RACES.map { it.name },
                                onSelect = homeViewModel::updateRace
                            )

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

                // ── Skills & Proficiencies ───────────────────────────────────
                item {
                    CollapsibleSection(
                        title    = "Skills & Proficiencies",
                        expanded = skillsExpanded,
                        onToggle = { skillsExpanded = !skillsExpanded }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            // Legend row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfDot(filled = true, color = AccentCrimson)
                                Spacer(Modifier.width(3.dp))
                                Text("Prof", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                Spacer(Modifier.width(10.dp))
                                ProfDot(filled = true, color = AccentGold)
                                Spacer(Modifier.width(3.dp))
                                Text("Exp", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Prof Bonus: ${fmtMod(profBonus)}",
                                    color = AccentCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(Modifier.height(4.dp))

                            DND_SKILLS.forEach { (skillName, abilityKey) ->
                                val score      = abilityScores[abilityKey] ?: 10
                                val isProficient = skillName in skillProf
                                val hasExpertise = skillName in skillExp
                                val totalMod   = skillModifier(score, profBonus, isProficient, hasExpertise)

                                SkillRow(
                                    skillName    = skillName,
                                    abilityLabel = abilityKey,
                                    totalMod     = totalMod,
                                    isProficient = isProficient,
                                    hasExpertise = hasExpertise,
                                    canRoll      = onSkillRoll != null,
                                    onToggleProf = { homeViewModel.toggleSkillProficiency(skillName) },
                                    onToggleExp  = { homeViewModel.toggleSkillExpertise(skillName) },
                                    onRoll       = { onSkillRoll?.invoke(totalMod, skillName) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Skills composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SkillRow(
    skillName: String,
    abilityLabel: String,
    totalMod: Int,
    isProficient: Boolean,
    hasExpertise: Boolean,
    canRoll: Boolean,
    onToggleProf: () -> Unit,
    onToggleExp: () -> Unit,
    onRoll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (canRoll) Modifier.clickable { onRoll() }
                else Modifier
            )
            .padding(vertical = 5.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Proficiency dot — tappable
        Box(
            modifier = Modifier
                .size(28.dp)
                .clickable { onToggleProf() },
            contentAlignment = Alignment.Center
        ) {
            ProfDot(filled = isProficient, color = AccentCrimson)
        }

        // Expertise dot — tappable only when proficient
        Box(
            modifier = Modifier
                .size(28.dp)
                .then(if (isProficient) Modifier.clickable { onToggleExp() } else Modifier),
            contentAlignment = Alignment.Center
        ) {
            ProfDot(filled = hasExpertise && isProficient, color = AccentGold)
        }

        Spacer(Modifier.width(4.dp))

        // Skill name
        Text(
            text = skillName,
            color = if (canRoll) AccentGold else Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        // Ability label
        Text(
            text = abilityLabel,
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            modifier = Modifier.padding(end = 10.dp)
        )

        // Total modifier
        Text(
            text = fmtMod(totalMod),
            color = if (totalMod >= 0) AccentGold else AccentCrimson,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(36.dp)
        )

        // Roll icon hint when in dice screen
        if (canRoll) {
            Spacer(Modifier.width(6.dp))
            Text("🎲", fontSize = 14.sp)
        }
    }
}

@Composable
private fun ProfDot(filled: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (filled) color else Color.Transparent)
            .border(1.5.dp, color, CircleShape)
    )
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
    val modifier_ = (score - 10) / 2
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
            Text(
                text       = stat,
                color      = AccentCrimson,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

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
