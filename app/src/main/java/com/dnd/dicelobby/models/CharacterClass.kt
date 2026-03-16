package com.dnd.dicelobby.models

/**
 * A D&D 5e character class with its core mechanics.
 * Data sourced from rpgbot.net and the D&D 5e SRD.
 */
data class CharacterClass(
    val name: String,
    val hitDie: String,
    val primaryAbility: String,
    val savingThrows: List<String>,
    val armorProficiencies: String,
    val weaponProficiencies: String,
    val skillChoices: List<String>,
    val skillCount: Int,
    val startingEquipment: List<String>,
    val keyFeatures: List<String>,
    val subclassLabel: String,
    val subclasses: List<String>
)

val DND_CLASSES = listOf(

    CharacterClass(
        name = "Artificer",
        hitDie = "d8",
        primaryAbility = "Intelligence",
        savingThrows = listOf("CON", "INT"),
        armorProficiencies = "Light, medium armor, shields",
        weaponProficiencies = "Simple weapons",
        skillChoices = listOf("Arcana", "History", "Investigation", "Medicine", "Nature", "Perception", "Sleight of Hand"),
        skillCount = 2,
        startingEquipment = listOf("Thieves' Tools", "Light Crossbow + 20 bolts", "Dungeoneer's Pack", "Leather Armor"),
        keyFeatures = listOf("Magical Tinkering", "Spellcasting (INT)", "Infuse Item", "The Right Tool for the Job", "Tool Expertise", "Flash of Genius"),
        subclassLabel = "Artificer Specialist",
        subclasses = listOf("Alchemist", "Armorer", "Artillerist", "Battle Smith")
    ),

    CharacterClass(
        name = "Barbarian",
        hitDie = "d12",
        primaryAbility = "Strength",
        savingThrows = listOf("STR", "CON"),
        armorProficiencies = "Light, medium armor, shields",
        weaponProficiencies = "Simple and martial weapons",
        skillChoices = listOf("Animal Handling", "Athletics", "Intimidation", "Nature", "Perception", "Survival"),
        skillCount = 2,
        startingEquipment = listOf("Greataxe or any martial melee weapon", "Two handaxes or any simple weapon", "Explorer's Pack", "4 Javelins"),
        keyFeatures = listOf("Rage (bonus damage + resistance to physical damage)", "Unarmored Defense (10+DEX+CON)", "Reckless Attack", "Danger Sense", "Extra Attack (lvl 5)", "Fast Movement (lvl 5)", "Feral Instinct (lvl 7)", "Brutal Critical (lvl 9)"),
        subclassLabel = "Primal Path",
        subclasses = listOf("Ancestral Guardian", "Battlerager", "Beast", "Berserker", "Storm Herald", "Totem Warrior", "Wild Magic", "Zealot")
    ),

    CharacterClass(
        name = "Bard",
        hitDie = "d8",
        primaryAbility = "Charisma",
        savingThrows = listOf("DEX", "CHA"),
        armorProficiencies = "Light armor",
        weaponProficiencies = "Simple weapons, hand crossbows, longswords, rapiers, shortswords",
        skillChoices = listOf("Any three skills"),
        skillCount = 3,
        startingEquipment = listOf("Rapier or longsword or any simple weapon", "Diplomat's Pack or Entertainer's Pack", "Lute or any musical instrument", "Leather Armor", "Dagger"),
        keyFeatures = listOf("Spellcasting (CHA)", "Bardic Inspiration (d6 to d12)", "Jack of All Trades (+half prof to non-proficient checks)", "Song of Rest", "Expertise (double prof on 2 skills)", "Countercharm", "Magical Secrets"),
        subclassLabel = "Bard College",
        subclasses = listOf("College of Creation", "College of Eloquence", "College of Glamour", "College of Lore", "College of Spirits", "College of Swords", "College of Valor", "College of Whispers")
    ),

    CharacterClass(
        name = "Cleric",
        hitDie = "d8",
        primaryAbility = "Wisdom",
        savingThrows = listOf("WIS", "CHA"),
        armorProficiencies = "Light, medium armor, shields",
        weaponProficiencies = "Simple weapons",
        skillChoices = listOf("History", "Insight", "Medicine", "Persuasion", "Religion"),
        skillCount = 2,
        startingEquipment = listOf("Mace or warhammer (if proficient)", "Scale mail or leather armor or chain mail", "Light crossbow + 20 bolts or any simple weapon", "Priest's Pack or Explorer's Pack", "Shield", "Holy Symbol"),
        keyFeatures = listOf("Spellcasting (WIS)", "Divine Domain (subclass at lvl 1)", "Channel Divinity", "Destroy Undead", "Divine Intervention", "Domain Spells"),
        subclassLabel = "Divine Domain",
        subclasses = listOf("Arcana", "Death", "Forge", "Grave", "Knowledge", "Life", "Light", "Nature", "Order", "Peace", "Tempest", "Trickery", "Twilight", "War")
    ),

    CharacterClass(
        name = "Druid",
        hitDie = "d8",
        primaryAbility = "Wisdom",
        savingThrows = listOf("INT", "WIS"),
        armorProficiencies = "Light, medium armor, shields (non-metal only)",
        weaponProficiencies = "Clubs, daggers, darts, javelins, maces, quarterstaffs, scimitars, sickles, slings, spears",
        skillChoices = listOf("Arcana", "Animal Handling", "Insight", "Medicine", "Nature", "Perception", "Religion", "Survival"),
        skillCount = 2,
        startingEquipment = listOf("Wooden shield or any simple weapon", "Scimitar or any simple melee weapon", "Leather Armor", "Explorer's Pack", "Druidic Focus"),
        keyFeatures = listOf("Druidic (secret language)", "Spellcasting (WIS)", "Wild Shape (transform into beasts)", "Timeless Body (1/10 aging at lvl 18)", "Beast Spells (lvl 18)"),
        subclassLabel = "Druid Circle",
        subclasses = listOf("Circle of Dreams", "Circle of Spores", "Circle of Stars", "Circle of the Land", "Circle of the Moon", "Circle of the Shepherd", "Circle of Wildfire")
    ),

    CharacterClass(
        name = "Fighter",
        hitDie = "d10",
        primaryAbility = "Strength or Dexterity",
        savingThrows = listOf("STR", "CON"),
        armorProficiencies = "All armor, shields",
        weaponProficiencies = "Simple and martial weapons",
        skillChoices = listOf("Acrobatics", "Animal Handling", "Athletics", "History", "Insight", "Intimidation", "Perception", "Survival"),
        skillCount = 2,
        startingEquipment = listOf("Chain mail or leather armor + longbow + 20 arrows", "Martial weapon + shield or two martial weapons", "Light crossbow + 20 bolts or two handaxes", "Dungeoneer's Pack or Explorer's Pack"),
        keyFeatures = listOf("Fighting Style", "Second Wind (bonus action heal d10+level)", "Action Surge (extra action, lvl 2)", "Extra Attack ×2 (lvl 5/11)", "Indomitable (reroll failed save, lvl 9)"),
        subclassLabel = "Martial Archetype",
        subclasses = listOf("Arcane Archer", "Battle Master", "Cavalier", "Champion", "Echo Knight", "Eldritch Knight", "Purple Dragon Knight", "Rune Knight", "Samurai")
    ),

    CharacterClass(
        name = "Monk",
        hitDie = "d8",
        primaryAbility = "Dexterity & Wisdom",
        savingThrows = listOf("STR", "DEX"),
        armorProficiencies = "None",
        weaponProficiencies = "Simple weapons, shortswords",
        skillChoices = listOf("Acrobatics", "Athletics", "History", "Insight", "Religion", "Stealth"),
        skillCount = 2,
        startingEquipment = listOf("Shortsword or any simple weapon", "Dungeoneer's Pack or Explorer's Pack", "10 darts"),
        keyFeatures = listOf("Unarmored Defense (10+DEX+WIS)", "Martial Arts (unarmed strike = d4 to d10)", "Ki Points", "Flurry of Blows", "Patient Defense", "Step of the Wind", "Slow Fall", "Stunning Strike", "Unarmored Movement"),
        subclassLabel = "Monastic Tradition",
        subclasses = listOf("Way of Mercy", "Way of Shadow", "Way of the Astral Self", "Way of the Drunken Master", "Way of the Four Elements", "Way of the Kensei", "Way of the Long Death", "Way of the Open Hand", "Way of the Sun Soul")
    ),

    CharacterClass(
        name = "Paladin",
        hitDie = "d10",
        primaryAbility = "Strength & Charisma",
        savingThrows = listOf("WIS", "CHA"),
        armorProficiencies = "All armor, shields",
        weaponProficiencies = "Simple and martial weapons",
        skillChoices = listOf("Athletics", "Insight", "Intimidation", "Medicine", "Persuasion", "Religion"),
        skillCount = 2,
        startingEquipment = listOf("Martial weapon + shield or two martial weapons", "Five javelins or any simple melee weapon", "Priest's Pack or Explorer's Pack", "Chain mail", "Holy Symbol"),
        keyFeatures = listOf("Divine Sense", "Lay On Hands (heal pool = lvl × 5)", "Fighting Style", "Spellcasting (CHA)", "Divine Smite (expend spell slot for radiant damage)", "Channel Divinity", "Aura of Protection (+CHA saves lvl 6)", "Aura of Courage (no frightened lvl 10)"),
        subclassLabel = "Sacred Oath",
        subclasses = listOf("Oath of Conquest", "Oath of Devotion", "Oath of Glory", "Oath of Redemption", "Oath of the Ancients", "Oath of the Crown", "Oath of the Watchers", "Oath of Vengeance", "Oathbreaker")
    ),

    CharacterClass(
        name = "Ranger",
        hitDie = "d10",
        primaryAbility = "Dexterity & Wisdom",
        savingThrows = listOf("STR", "DEX"),
        armorProficiencies = "Light, medium armor, shields",
        weaponProficiencies = "Simple and martial weapons",
        skillChoices = listOf("Acrobatics", "Animal Handling", "Arcana", "Athletics", "Insight", "Investigation", "Nature", "Perception", "Stealth", "Survival"),
        skillCount = 3,
        startingEquipment = listOf("Scale mail or leather armor", "Two shortswords or two simple melee weapons", "Dungeoneer's Pack or Explorer's Pack", "Longbow + quiver of 20 arrows"),
        keyFeatures = listOf("Favored Enemy", "Natural Explorer", "Fighting Style", "Spellcasting (WIS)", "Primeval Awareness", "Extra Attack (lvl 5)", "Land's Stride", "Hide in Plain Sight", "Vanish"),
        subclassLabel = "Ranger Conclave",
        subclasses = listOf("Beast Master", "Drakewarden", "Fey Wanderer", "Gloom Stalker", "Horizon Walker", "Hunter", "Monster Slayer", "Swarm Keeper")
    ),

    CharacterClass(
        name = "Rogue",
        hitDie = "d8",
        primaryAbility = "Dexterity",
        savingThrows = listOf("DEX", "INT"),
        armorProficiencies = "Light armor",
        weaponProficiencies = "Simple weapons, hand crossbows, longswords, rapiers, shortswords",
        skillChoices = listOf("Acrobatics", "Athletics", "Deception", "Insight", "Intimidation", "Investigation", "Perception", "Performance", "Persuasion", "Sleight of Hand", "Stealth"),
        skillCount = 4,
        startingEquipment = listOf("Rapier or shortsword", "Shortbow + quiver of 20 arrows or shortsword", "Burglar's Pack or Dungeoneer's Pack or Explorer's Pack", "Leather Armor", "Two daggers", "Thieves' Tools"),
        keyFeatures = listOf("Expertise (double prof on 2 skills)", "Sneak Attack (1d6 to 10d6)", "Thieves' Cant", "Cunning Action (Dash/Disengage/Hide as bonus action)", "Uncanny Dodge", "Evasion (DEX save: no damage on success, half on fail)", "Reliable Talent"),
        subclassLabel = "Roguish Archetype",
        subclasses = listOf("Arcane Trickster", "Assassin", "Inquisitive", "Mastermind", "Phantom", "Scout", "Soulknife", "Swashbuckler", "Thief")
    ),

    CharacterClass(
        name = "Sorcerer",
        hitDie = "d6",
        primaryAbility = "Charisma",
        savingThrows = listOf("CON", "CHA"),
        armorProficiencies = "None",
        weaponProficiencies = "Daggers, darts, slings, quarterstaffs, light crossbows",
        skillChoices = listOf("Arcana", "Deception", "Insight", "Intimidation", "Persuasion", "Religion"),
        skillCount = 2,
        startingEquipment = listOf("Light crossbow + 20 bolts or any simple weapon", "Component pouch or arcane focus", "Dungeoneer's Pack or Explorer's Pack", "Two daggers"),
        keyFeatures = listOf("Spellcasting (CHA)", "Sorcerous Origin (subclass at lvl 1)", "Font of Magic (Sorcery Points)", "Metamagic (modify spells)", "Sorcerous Restoration"),
        subclassLabel = "Sorcerous Origin",
        subclasses = listOf("Aberrant Mind", "Clockwork Soul", "Divine Soul", "Draconic Bloodline", "Lunar Sorcery", "Shadow Magic", "Storm Sorcery", "Wild Magic")
    ),

    CharacterClass(
        name = "Warlock",
        hitDie = "d8",
        primaryAbility = "Charisma",
        savingThrows = listOf("WIS", "CHA"),
        armorProficiencies = "Light armor",
        weaponProficiencies = "Simple weapons",
        skillChoices = listOf("Arcana", "Deception", "History", "Intimidation", "Investigation", "Nature", "Religion"),
        skillCount = 2,
        startingEquipment = listOf("Light crossbow + 20 bolts or any simple weapon", "Component pouch or arcane focus", "Scholar's Pack or Dungeoneer's Pack", "Leather Armor", "Any simple weapon", "Two daggers"),
        keyFeatures = listOf("Otherworldly Patron (subclass at lvl 1)", "Pact Magic (short rest recharge)", "Eldritch Invocations", "Pact Boon (Pact of the Blade/Chain/Rod/Talisman)", "Mystic Arcanum", "Eldritch Master"),
        subclassLabel = "Otherworldly Patron",
        subclasses = listOf("The Archfey", "The Celestial", "The Fathomless", "The Fiend", "The Genie", "The Great Old One", "The Hexblade", "The Undying")
    ),

    CharacterClass(
        name = "Wizard",
        hitDie = "d6",
        primaryAbility = "Intelligence",
        savingThrows = listOf("INT", "WIS"),
        armorProficiencies = "None",
        weaponProficiencies = "Daggers, darts, slings, quarterstaffs, light crossbows",
        skillChoices = listOf("Arcana", "History", "Insight", "Investigation", "Medicine", "Religion"),
        skillCount = 2,
        startingEquipment = listOf("Quarterstaff or dagger", "Component pouch or arcane focus", "Scholar's Pack or Explorer's Pack", "Spellbook"),
        keyFeatures = listOf("Spellcasting (INT)", "Arcane Recovery (regain slots on short rest)", "Spell Mastery (cast at-will at lvl 18)", "Signature Spells (lvl 20)"),
        subclassLabel = "Arcane Tradition",
        subclasses = listOf("Bladesinging", "Chronurgy Magic", "Graviturgy Magic", "Order of the Scribes", "School of Abjuration", "School of Conjuration", "School of Divination", "School of Enchantment", "School of Evocation", "School of Illusion", "School of Necromancy", "School of Transmutation", "War Magic")
    )
)
