package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dnd.dicelobby.models.*
import com.dnd.dicelobby.ui.theme.AccentGold
import com.dnd.dicelobby.ui.theme.AccentCrimson
import com.dnd.dicelobby.ui.viewmodels.HomeViewModel

private val BG_GRADIENT = listOf(Color(0xFF0D0B14), Color(0xFF1A0A1E), Color(0xFF0D0B14))
private val SURFACE_COLOR = Color(0xFF1E1A2E)

/**
 * Multi-tab character creation screen: Race → Class → Equipment.
 * Selections are persisted via [HomeViewModel] and shown on the Home screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val selectedRace     by homeViewModel.playerRace.collectAsStateWithLifecycle()
    val selectedSubrace  by homeViewModel.playerSubrace.collectAsStateWithLifecycle()
    val selectedClass    by homeViewModel.playerClass.collectAsStateWithLifecycle()
    val selectedSubclass by homeViewModel.playerSubclass.collectAsStateWithLifecycle()
    val selectedArmor    by homeViewModel.startingArmor.collectAsStateWithLifecycle()
    val selectedWeapon   by homeViewModel.startingWeapon.collectAsStateWithLifecycle()
    val selectedGear     by homeViewModel.startingGear.collectAsStateWithLifecycle()

    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Race", "Class", "Equipment")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Build Character", color = AccentGold, fontWeight = FontWeight.Bold) },
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
                .background(Brush.verticalGradient(BG_GRADIENT))
                .padding(padding)
        ) {
            Column(Modifier.fillMaxSize()) {
                // Tab row
                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor   = Color(0xFF0D0B14),
                    contentColor     = AccentGold,
                    indicator        = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                            color    = AccentGold
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = tabIndex == index,
                            onClick  = { tabIndex = index },
                            text     = {
                                Text(
                                    text       = title,
                                    fontWeight = if (tabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color      = if (tabIndex == index) AccentGold else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        )
                    }
                }

                when (tabIndex) {
                    0 -> RaceTab(
                        selectedRace    = selectedRace,
                        selectedSubrace = selectedSubrace,
                        onRaceSelected  = homeViewModel::updateRace,
                        onSubraceSelected = homeViewModel::updateSubrace
                    )
                    1 -> ClassTab(
                        selectedClass    = selectedClass,
                        selectedSubclass = selectedSubclass,
                        onClassSelected  = homeViewModel::updateClass,
                        onSubclassSelected = homeViewModel::updateSubclass
                    )
                    2 -> EquipmentTab(
                        selectedArmor  = selectedArmor,
                        selectedWeapon = selectedWeapon,
                        selectedGear   = selectedGear,
                        onArmorSelected  = homeViewModel::updateArmor,
                        onWeaponSelected = homeViewModel::updateWeapon,
                        onGearToggled    = homeViewModel::toggleGearItem
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Race Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RaceTab(
    selectedRace: String,
    selectedSubrace: String,
    onRaceSelected: (String) -> Unit,
    onSubraceSelected: (String) -> Unit
) {
    val raceData = DND_RACES.find { it.name == selectedRace }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionLabel("Choose Race")
        }

        // Race grid — chunked into rows of 3 to avoid nested lazy layout issues
        items(DND_RACES.chunked(3)) { rowRaces ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowRaces.forEach { race ->
                    Box(modifier = Modifier.weight(1f)) {
                        SelectableChip(
                            label      = race.name,
                            isSelected = race.name == selectedRace,
                            onClick    = { onRaceSelected(race.name) }
                        )
                    }
                }
                // Fill remaining columns if row is short
                repeat(3 - rowRaces.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        // Selected race detail card
        if (raceData != null) {
            item {
                RaceDetailCard(raceData)
            }

            // Subrace picker
            if (raceData.subraces.isNotEmpty()) {
                item { SectionLabel("Choose Subrace") }
                items(raceData.subraces) { subrace ->
                    SubraceRow(
                        subrace    = subrace,
                        isSelected = subrace.name == selectedSubrace,
                        onClick    = { onSubraceSelected(subrace.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RaceDetailCard(race: Race) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SURFACE_COLOR),
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(race.name, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            InfoRow("Size / Speed", "${race.size}  ·  ${race.speed} ft")
            if (race.abilityBonuses.isNotEmpty()) {
                InfoRow("Ability Bonuses", race.abilityBonuses.entries.joinToString("  ") { "+${it.value} ${it.key}" })
            }
            InfoRow("Languages", race.languages.joinToString(", "))
            if (race.traits.isNotEmpty()) {
                Text("Traits", color = AccentGold.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                race.traits.forEach { trait ->
                    Text("• $trait", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SubraceRow(subrace: Subrace, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape   = RoundedCornerShape(10.dp),
        colors  = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.2f) else Color.Transparent,
            contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.85f)
        ),
        border  = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentGold else Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(subrace.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
            if (subrace.abilityBonuses.isNotEmpty()) {
                Text(
                    subrace.abilityBonuses.entries.joinToString("  ") { "+${it.value} ${it.key}" },
                    color    = AccentGold.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
            subrace.traits.forEach { t ->
                Text("• $t", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Class Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ClassTab(
    selectedClass: String,
    selectedSubclass: String,
    onClassSelected: (String) -> Unit,
    onSubclassSelected: (String) -> Unit
) {
    val classData = DND_CLASSES.find { it.name == selectedClass }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionLabel("Choose Class") }

        // Class grid — chunked rows to avoid nested lazy layout
        items(DND_CLASSES.chunked(3)) { rowClasses ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowClasses.forEach { cls ->
                    Box(modifier = Modifier.weight(1f)) {
                        SelectableChip(
                            label      = cls.name,
                            isSelected = cls.name == selectedClass,
                            onClick    = { onClassSelected(cls.name) }
                        )
                    }
                }
                repeat(3 - rowClasses.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        if (classData != null) {
            item { ClassDetailCard(classData) }

            item { SectionLabel(classData.subclassLabel) }
            items(classData.subclasses) { sub ->
                SelectableRow(
                    label      = sub,
                    isSelected = sub == selectedSubclass,
                    onClick    = { onSubclassSelected(sub) }
                )
            }
        }
    }
}

@Composable
private fun ClassDetailCard(cls: CharacterClass) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SURFACE_COLOR),
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(cls.name, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            InfoRow("Hit Die", "d${cls.hitDie}")
            InfoRow("Primary Ability", cls.primaryAbility)
            InfoRow("Saves", cls.savingThrows.joinToString(", "))
            Text("Features", color = AccentGold.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            cls.keyFeatures.forEach { f ->
                Text("• $f", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Equipment Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EquipmentTab(
    selectedArmor: String,
    selectedWeapon: String,
    selectedGear: List<String>,
    onArmorSelected: (String) -> Unit,
    onWeaponSelected: (String) -> Unit,
    onGearToggled: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionLabel("Starting Armor") }
        items(listOf("Leather Armor", "Scale Mail", "Chain Mail")) { armor ->
            SelectableRow(label = armor, isSelected = armor == selectedArmor, onClick = { onArmorSelected(armor) })
        }

        item { SectionLabel("Starting Weapon") }
        items(listOf("Longsword", "Shortbow", "Dagger", "Greataxe")) { weapon ->
            SelectableRow(label = weapon, isSelected = weapon == selectedWeapon, onClick = { onWeaponSelected(weapon) })
        }

        item { SectionLabel("Adventuring Gear") }
        items(listOf("Explorer's Pack", "Burglar's Pack", "Priest's Pack", "Scholar's Pack")) { gear ->
            SelectableRow(
                label      = gear,
                isSelected = gear in selectedGear,
                onClick    = { onGearToggled(gear) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared UI Components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text.uppercase(),
        color      = AccentGold,
        fontSize   = 12.sp,
        fontWeight = FontWeight.Black,
        modifier   = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SelectableChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color   = if (isSelected) AccentGold else SURFACE_COLOR,
        shape   = RoundedCornerShape(8.dp),
        border  = BorderStroke(1.dp, if (isSelected) AccentGold else Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(
                text     = label,
                color    = if (isSelected) Color.Black else Color.White,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SelectableRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color   = if (isSelected) Color.White.copy(alpha = 0.05f) else Color.Transparent,
        shape   = RoundedCornerShape(8.dp),
        border  = BorderStroke(1.dp, if (isSelected) AccentGold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = if (isSelected) AccentGold else Color.White, fontSize = 14.sp)
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
            }
        }
    }
}
