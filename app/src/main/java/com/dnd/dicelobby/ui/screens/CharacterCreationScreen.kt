package com.dnd.dicelobby.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
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
        colors  = OutlinedButtonDefaults.outlinedButtonColors(
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
        colors   = CardDefaults.cardColors(containerColor = SURFACE_COLOR),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBadge("Hit Die", cls.hitDie)
                StatBadge("Primary", cls.primaryAbility)
                StatBadge("Saves", cls.savingThrows.joinToString("/"))
            }
            InfoRow("Armor", cls.armorProficiencies)
            InfoRow("Weapons", cls.weaponProficiencies)
            InfoRow("Skills", "Choose ${cls.skillCount}: ${cls.skillChoices.joinToString(", ")}")
            Text("Key Features", color = AccentGold.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            cls.keyFeatures.forEach { f ->
                Text("• $f", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            Text("Starting Equipment", color = AccentGold.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            cls.startingEquipment.forEach { e ->
                Text("• $e", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
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
    var armorCat   by remember { mutableStateOf(ArmorCategory.LIGHT) }
    var weaponCat  by remember { mutableStateOf(WeaponCategory.SIMPLE_MELEE) }
    var gearCat    by remember { mutableStateOf(GearCategory.CONTAINER) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Armor ──────────────────────────────────────────────
        item { SectionLabel("Armor") }
        item {
            CategoryFilter(
                options   = ArmorCategory.entries,
                selected  = armorCat,
                label     = { it.name.replace("_", " ").lowercase().replaceFirstChar { c -> c.uppercase() } },
                onSelect  = { armorCat = it }
            )
        }
        items(ALL_ARMOR.filter { it.category == armorCat }) { armor ->
            EquipmentRow(
                name       = armor.name,
                detail     = buildString {
                    append("AC ${armor.baseAc}")
                    if (armor.addDex) append(if (armor.maxDex != null) "+DEX (max ${armor.maxDex})" else "+DEX")
                    if (armor.strRequirement > 0) append(" · STR ${armor.strRequirement}+")
                    if (armor.stealthDisadv) append(" · Stealth disadv")
                    append("  ${armor.cost}")
                },
                isSelected = armor.name == selectedArmor,
                onClick    = { onArmorSelected(armor.name) }
            )
        }

        // ── Weapons ────────────────────────────────────────────
        item { SectionLabel("Weapons") }
        item {
            CategoryFilter(
                options  = WeaponCategory.entries,
                selected = weaponCat,
                label    = { it.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } } },
                onSelect = { weaponCat = it }
            )
        }
        items(ALL_WEAPONS.filter { it.category == weaponCat }) { weapon ->
            EquipmentRow(
                name       = weapon.name,
                detail     = buildString {
                    append("${weapon.damage} ${weapon.damageType}")
                    if (weapon.range != "—") append(" · ${weapon.range}")
                    if (weapon.properties.isNotEmpty()) append(" · ${weapon.properties.joinToString(", ")}")
                    append("  ${weapon.cost}")
                },
                isSelected = weapon.name == selectedWeapon,
                onClick    = { onWeaponSelected(weapon.name) }
            )
        }

        // ── Adventuring Gear ───────────────────────────────────
        item { SectionLabel("Adventuring Gear") }
        item {
            CategoryFilter(
                options  = GearCategory.entries,
                selected = gearCat,
                label    = { it.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } } },
                onSelect = { gearCat = it }
            )
        }
        items(ALL_GEAR.filter { it.category == gearCat }) { gear ->
            GearRow(
                name       = gear.name,
                detail     = "${gear.description}  ${gear.cost}",
                isSelected = selectedGear.contains(gear.name),
                onToggle   = { onGearToggled(gear.name) }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared UI components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text       = text.uppercase(),
        color      = AccentGold,
        fontSize   = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun SelectableChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape   = RoundedCornerShape(10.dp),
        colors  = OutlinedButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.25f) else Color.Transparent,
            contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.8f)
        ),
        border  = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentGold else Color.White.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SelectableRow(label: String, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        shape    = RoundedCornerShape(10.dp),
        colors   = OutlinedButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.2f) else Color.Transparent,
            contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.85f)
        ),
        border   = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentGold else Color.White.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
    }
}

@Composable
private fun EquipmentRow(name: String, detail: String, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick  = onClick,
        shape    = RoundedCornerShape(10.dp),
        colors   = OutlinedButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.2f) else Color.Transparent,
            contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.85f)
        ),
        border   = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentGold else Color.White.copy(alpha = 0.15f)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
            Text(detail, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun GearRow(name: String, detail: String, isSelected: Boolean, onToggle: () -> Unit) {
    OutlinedButton(
        onClick  = onToggle,
        shape    = RoundedCornerShape(10.dp),
        colors   = OutlinedButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) AccentCrimson.copy(alpha = 0.2f) else Color.Transparent,
            contentColor   = if (isSelected) AccentGold else Color.White.copy(alpha = 0.85f)
        ),
        border   = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentGold else Color.White.copy(alpha = 0.15f)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                Text(detail, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun <T> CategoryFilter(options: Iterable<T>, selected: T, label: (T) -> String, onSelect: (T) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(options.toList()) { opt ->
            val isSel = opt == selected
            FilterChip(
                selected = isSel,
                onClick  = { onSelect(opt) },
                label    = { Text(label(opt), fontSize = 11.sp) },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentCrimson.copy(alpha = 0.25f),
                    selectedLabelColor     = AccentGold,
                    containerColor         = Color.Transparent,
                    labelColor             = Color.White.copy(alpha = 0.6f)
                ),
                border   = FilterChipDefaults.filterChipBorder(
                    enabled          = true,
                    selected         = isSel,
                    selectedBorderColor = AccentGold,
                    borderColor      = Color.White.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label:", color = AccentGold.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
private fun StatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
    }
}
