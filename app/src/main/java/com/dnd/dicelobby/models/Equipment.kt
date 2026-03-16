package com.dnd.dicelobby.models

/**
 * D&D 5e equipment data sourced from rpgbot.net and the D&D 5e SRD.
 */

// ─── Armor ────────────────────────────────────────────────────────────────────

enum class ArmorCategory { LIGHT, MEDIUM, HEAVY, SHIELD }

data class Armor(
    val name: String,
    val category: ArmorCategory,
    val baseAc: Int,
    val addDex: Boolean,
    val maxDex: Int?,          // null = unlimited
    val strRequirement: Int = 0,
    val stealthDisadv: Boolean = false,
    val cost: String,
    val weight: String
)

val ALL_ARMOR = listOf(
    // Light
    Armor("Padded",          ArmorCategory.LIGHT,  11, true,  null, stealthDisadv = true,  cost = "5 gp",    weight = "8 lb"),
    Armor("Leather",         ArmorCategory.LIGHT,  11, true,  null,                         cost = "10 gp",   weight = "10 lb"),
    Armor("Studded Leather", ArmorCategory.LIGHT,  12, true,  null,                         cost = "45 gp",   weight = "13 lb"),
    // Medium
    Armor("Hide",            ArmorCategory.MEDIUM, 12, true,  2,                            cost = "10 gp",   weight = "12 lb"),
    Armor("Chain Shirt",     ArmorCategory.MEDIUM, 13, true,  2,                            cost = "50 gp",   weight = "20 lb"),
    Armor("Scale Mail",      ArmorCategory.MEDIUM, 14, true,  2, stealthDisadv = true,      cost = "50 gp",   weight = "45 lb"),
    Armor("Breastplate",     ArmorCategory.MEDIUM, 14, true,  2,                            cost = "400 gp",  weight = "20 lb"),
    Armor("Half Plate",      ArmorCategory.MEDIUM, 15, true,  2, stealthDisadv = true,      cost = "750 gp",  weight = "40 lb"),
    // Heavy
    Armor("Ring Mail",       ArmorCategory.HEAVY,  14, false, 0, stealthDisadv = true,      cost = "30 gp",   weight = "40 lb"),
    Armor("Chain Mail",      ArmorCategory.HEAVY,  16, false, 0, strRequirement = 13, stealthDisadv = true, cost = "75 gp",  weight = "55 lb"),
    Armor("Splint",          ArmorCategory.HEAVY,  17, false, 0, strRequirement = 15, stealthDisadv = true, cost = "200 gp", weight = "60 lb"),
    Armor("Plate",           ArmorCategory.HEAVY,  18, false, 0, strRequirement = 15, stealthDisadv = true, cost = "1500 gp",weight = "65 lb"),
    // Shield
    Armor("Shield",          ArmorCategory.SHIELD,  2, false, null,                         cost = "10 gp",   weight = "6 lb")
)

// ─── Weapons ──────────────────────────────────────────────────────────────────

enum class WeaponCategory { SIMPLE_MELEE, SIMPLE_RANGED, MARTIAL_MELEE, MARTIAL_RANGED }

data class Weapon(
    val name: String,
    val category: WeaponCategory,
    val damage: String,
    val damageType: String,
    val properties: List<String>,
    val range: String = "—",
    val cost: String,
    val weight: String
)

val ALL_WEAPONS = listOf(
    // ── Simple Melee ──────────────────────────────────────
    Weapon("Club",          WeaponCategory.SIMPLE_MELEE,  "1d4",  "Bludgeoning", listOf("Light"),                              cost = "1 sp",  weight = "2 lb"),
    Weapon("Dagger",        WeaponCategory.SIMPLE_MELEE,  "1d4",  "Piercing",    listOf("Finesse", "Light", "Thrown"),         range = "20/60",  cost = "2 gp",  weight = "1 lb"),
    Weapon("Greatclub",     WeaponCategory.SIMPLE_MELEE,  "1d8",  "Bludgeoning", listOf("Two-Handed"),                         cost = "2 sp",  weight = "10 lb"),
    Weapon("Handaxe",       WeaponCategory.SIMPLE_MELEE,  "1d6",  "Slashing",    listOf("Light", "Thrown"),                    range = "20/60",  cost = "5 gp",  weight = "2 lb"),
    Weapon("Javelin",       WeaponCategory.SIMPLE_MELEE,  "1d6",  "Piercing",    listOf("Thrown"),                             range = "30/120", cost = "5 sp",  weight = "2 lb"),
    Weapon("Light Hammer",  WeaponCategory.SIMPLE_MELEE,  "1d4",  "Bludgeoning", listOf("Light", "Thrown"),                    range = "20/60",  cost = "2 gp",  weight = "2 lb"),
    Weapon("Mace",          WeaponCategory.SIMPLE_MELEE,  "1d6",  "Bludgeoning", emptyList(),                                  cost = "5 gp",  weight = "4 lb"),
    Weapon("Quarterstaff",  WeaponCategory.SIMPLE_MELEE,  "1d6",  "Bludgeoning", listOf("Versatile (1d8)"),                    cost = "2 sp",  weight = "4 lb"),
    Weapon("Sickle",        WeaponCategory.SIMPLE_MELEE,  "1d4",  "Slashing",    listOf("Light"),                              cost = "1 gp",  weight = "2 lb"),
    Weapon("Spear",         WeaponCategory.SIMPLE_MELEE,  "1d6",  "Piercing",    listOf("Thrown", "Versatile (1d8)"),          range = "20/60",  cost = "1 gp",  weight = "3 lb"),
    // ── Simple Ranged ─────────────────────────────────────
    Weapon("Light Crossbow",WeaponCategory.SIMPLE_RANGED, "1d8",  "Piercing",    listOf("Ammunition", "Loading", "Two-Handed"),range = "80/320", cost = "25 gp", weight = "5 lb"),
    Weapon("Dart",          WeaponCategory.SIMPLE_RANGED, "1d4",  "Piercing",    listOf("Finesse", "Thrown"),                  range = "20/60",  cost = "5 cp",  weight = "1/4 lb"),
    Weapon("Shortbow",      WeaponCategory.SIMPLE_RANGED, "1d6",  "Piercing",    listOf("Ammunition", "Two-Handed"),           range = "80/320", cost = "25 gp", weight = "2 lb"),
    Weapon("Sling",         WeaponCategory.SIMPLE_RANGED, "1d4",  "Bludgeoning", listOf("Ammunition"),                         range = "30/120", cost = "1 sp",  weight = "0"),
    // ── Martial Melee ─────────────────────────────────────
    Weapon("Battleaxe",     WeaponCategory.MARTIAL_MELEE, "1d8",  "Slashing",    listOf("Versatile (1d10)"),                   cost = "10 gp", weight = "4 lb"),
    Weapon("Flail",         WeaponCategory.MARTIAL_MELEE, "1d8",  "Bludgeoning", emptyList(),                                  cost = "10 gp", weight = "2 lb"),
    Weapon("Glaive",        WeaponCategory.MARTIAL_MELEE, "1d10", "Slashing",    listOf("Heavy", "Reach", "Two-Handed"),       cost = "20 gp", weight = "6 lb"),
    Weapon("Greataxe",      WeaponCategory.MARTIAL_MELEE, "1d12", "Slashing",    listOf("Heavy", "Two-Handed"),                cost = "30 gp", weight = "7 lb"),
    Weapon("Greatsword",    WeaponCategory.MARTIAL_MELEE, "2d6",  "Slashing",    listOf("Heavy", "Two-Handed"),                cost = "50 gp", weight = "6 lb"),
    Weapon("Halberd",       WeaponCategory.MARTIAL_MELEE, "1d10", "Slashing",    listOf("Heavy", "Reach", "Two-Handed"),       cost = "20 gp", weight = "6 lb"),
    Weapon("Lance",         WeaponCategory.MARTIAL_MELEE, "1d12", "Piercing",    listOf("Reach", "Special"),                   cost = "10 gp", weight = "6 lb"),
    Weapon("Longsword",     WeaponCategory.MARTIAL_MELEE, "1d8",  "Slashing",    listOf("Versatile (1d10)"),                   cost = "15 gp", weight = "3 lb"),
    Weapon("Maul",          WeaponCategory.MARTIAL_MELEE, "2d6",  "Bludgeoning", listOf("Heavy", "Two-Handed"),                cost = "10 gp", weight = "10 lb"),
    Weapon("Morningstar",   WeaponCategory.MARTIAL_MELEE, "1d8",  "Piercing",    emptyList(),                                  cost = "15 gp", weight = "4 lb"),
    Weapon("Pike",          WeaponCategory.MARTIAL_MELEE, "1d10", "Piercing",    listOf("Heavy", "Reach", "Two-Handed"),       cost = "5 gp",  weight = "18 lb"),
    Weapon("Rapier",        WeaponCategory.MARTIAL_MELEE, "1d8",  "Piercing",    listOf("Finesse"),                            cost = "25 gp", weight = "2 lb"),
    Weapon("Scimitar",      WeaponCategory.MARTIAL_MELEE, "1d6",  "Slashing",    listOf("Finesse", "Light"),                   cost = "25 gp", weight = "3 lb"),
    Weapon("Shortsword",    WeaponCategory.MARTIAL_MELEE, "1d6",  "Piercing",    listOf("Finesse", "Light"),                   cost = "10 gp", weight = "2 lb"),
    Weapon("Trident",       WeaponCategory.MARTIAL_MELEE, "1d6",  "Piercing",    listOf("Thrown", "Versatile (1d8)"),          range = "20/60", cost = "5 gp",  weight = "4 lb"),
    Weapon("War Pick",      WeaponCategory.MARTIAL_MELEE, "1d8",  "Piercing",    emptyList(),                                  cost = "5 gp",  weight = "2 lb"),
    Weapon("Warhammer",     WeaponCategory.MARTIAL_MELEE, "1d8",  "Bludgeoning", listOf("Versatile (1d10)"),                   cost = "15 gp", weight = "2 lb"),
    Weapon("Whip",          WeaponCategory.MARTIAL_MELEE, "1d4",  "Slashing",    listOf("Finesse", "Reach"),                   cost = "2 gp",  weight = "3 lb"),
    // ── Martial Ranged ────────────────────────────────────
    Weapon("Blowgun",       WeaponCategory.MARTIAL_RANGED,"1",    "Piercing",    listOf("Ammunition", "Loading"),              range = "25/100",  cost = "10 gp", weight = "1 lb"),
    Weapon("Hand Crossbow", WeaponCategory.MARTIAL_RANGED,"1d6",  "Piercing",    listOf("Ammunition", "Light", "Loading"),     range = "30/120",  cost = "75 gp", weight = "3 lb"),
    Weapon("Heavy Crossbow",WeaponCategory.MARTIAL_RANGED,"1d10", "Piercing",    listOf("Ammunition", "Heavy", "Loading", "Two-Handed"), range = "100/400", cost = "50 gp", weight = "18 lb"),
    Weapon("Longbow",       WeaponCategory.MARTIAL_RANGED,"1d8",  "Piercing",    listOf("Ammunition", "Heavy", "Two-Handed"), range = "150/600", cost = "50 gp", weight = "2 lb"),
    Weapon("Net",           WeaponCategory.MARTIAL_RANGED,"—",    "—",           listOf("Special", "Thrown"),                  range = "5/15",    cost = "1 gp",  weight = "3 lb")
)

// ─── Adventuring Gear ─────────────────────────────────────────────────────────

enum class GearCategory {
    CONTAINER, LIGHT, TOOL, ROPE_CLIMBING, RATIONS_SURVIVAL,
    HOLY_ARCANE, HEALING, MISC
}

data class AdventuringGear(
    val name: String,
    val category: GearCategory,
    val description: String,
    val cost: String,
    val weight: String
)

val ALL_GEAR = listOf(
    // Containers
    AdventuringGear("Backpack",         GearCategory.CONTAINER,        "Holds 30 lb / 1 cu ft",                        "2 gp",   "5 lb"),
    AdventuringGear("Chest",            GearCategory.CONTAINER,        "Holds 300 lb / 12 cu ft",                      "5 gp",   "25 lb"),
    AdventuringGear("Component Pouch",  GearCategory.CONTAINER,        "Material spell components (non-costly)",        "25 gp",  "2 lb"),
    AdventuringGear("Pouch",            GearCategory.CONTAINER,        "Holds 1/5 cu ft / 6 lb",                       "5 sp",   "1 lb"),
    AdventuringGear("Quiver",           GearCategory.CONTAINER,        "Holds 20 arrows or 20 bolts",                  "1 gp",   "1 lb"),
    AdventuringGear("Sack",             GearCategory.CONTAINER,        "Holds 1 cu ft / 30 lb",                        "1 cp",   "0.5 lb"),
    // Light
    AdventuringGear("Candle",           GearCategory.LIGHT,            "5 ft bright + 5 ft dim light, 1 hour",         "1 cp",   "—"),
    AdventuringGear("Lamp",             GearCategory.LIGHT,            "15 ft bright + 30 ft dim light, 6 hours/oil",  "5 sp",   "1 lb"),
    AdventuringGear("Bullseye Lantern", GearCategory.LIGHT,            "60 ft cone bright + 60 ft dim, 6 hours/oil",   "10 gp",  "2 lb"),
    AdventuringGear("Hooded Lantern",   GearCategory.LIGHT,            "30 ft bright + 30 ft dim, 6 hours/oil",        "5 gp",   "2 lb"),
    AdventuringGear("Torch",            GearCategory.LIGHT,            "20 ft bright + 20 ft dim light, 1 hour",       "1 cp",   "1 lb"),
    AdventuringGear("Tinderbox",        GearCategory.LIGHT,            "Flint, steel, and tinder — start fires",       "5 sp",   "1 lb"),
    AdventuringGear("Oil (flask)",      GearCategory.LIGHT,            "Fuel for lamps; can splash (2d6 fire dmg)",    "1 sp",   "1 lb"),
    // Tools
    AdventuringGear("Thieves' Tools",   GearCategory.TOOL,             "Pick locks + disarm traps (DEX proficiency)",  "25 gp",  "1 lb"),
    AdventuringGear("Herbalism Kit",    GearCategory.TOOL,             "Craft antitoxin and potions of healing",       "5 gp",   "3 lb"),
    AdventuringGear("Alchemist's Supplies",GearCategory.TOOL,          "Craft alchemical items and identify substances","50 gp",  "8 lb"),
    AdventuringGear("Disguise Kit",     GearCategory.TOOL,             "Create disguises (CHA/DEX proficiency)",       "25 gp",  "3 lb"),
    AdventuringGear("Forgery Kit",      GearCategory.TOOL,             "Duplicate documents and handwriting",          "15 gp",  "5 lb"),
    AdventuringGear("Healer's Kit",     GearCategory.HEALING,          "Stabilize creature at 0 HP (10 uses)",        "5 gp",   "3 lb"),
    // Rope & Climbing
    AdventuringGear("Rope, Hempen (50 ft)", GearCategory.ROPE_CLIMBING,"Standard rope, 2 HP, can hold 3 creatures", "1 gp",   "10 lb"),
    AdventuringGear("Rope, Silk (50 ft)",   GearCategory.ROPE_CLIMBING,"Lightweight, AC 12, 3 HP",                  "10 gp",  "5 lb"),
    AdventuringGear("Grappling Hook",   GearCategory.ROPE_CLIMBING,    "Anchor rope to surfaces",                      "2 gp",   "4 lb"),
    AdventuringGear("Piton",            GearCategory.ROPE_CLIMBING,    "Spike for anchoring rope in rock",             "5 cp",   "1/4 lb"),
    AdventuringGear("Hammer",           GearCategory.ROPE_CLIMBING,    "Drive pitons or general carpentry",            "1 gp",   "3 lb"),
    AdventuringGear("Crowbar",          GearCategory.ROPE_CLIMBING,    "Advantage on STR checks to pry things open",   "2 gp",   "5 lb"),
    // Rations & Survival
    AdventuringGear("Rations (1 day)",  GearCategory.RATIONS_SURVIVAL, "Dried food for 1 day in the wilderness",       "5 sp",   "2 lb"),
    AdventuringGear("Waterskin",        GearCategory.RATIONS_SURVIVAL, "Holds 4 pints of liquid",                      "2 sp",   "5 lb (full)"),
    AdventuringGear("Bedroll",          GearCategory.RATIONS_SURVIVAL, "Blanket + pad for sleeping outdoors",          "1 gp",   "7 lb"),
    AdventuringGear("Blanket",          GearCategory.RATIONS_SURVIVAL, "Warmth in cold environments",                  "5 sp",   "3 lb"),
    AdventuringGear("Hunting Trap",     GearCategory.RATIONS_SURVIVAL, "Large beast: DC 13 STR save or restrained",    "5 gp",   "25 lb"),
    // Holy / Arcane Focuses
    AdventuringGear("Holy Symbol",      GearCategory.HOLY_ARCANE,      "Spellcasting focus for Clerics and Paladins",  "5 gp",   "1 lb"),
    AdventuringGear("Holy Water (flask)",GearCategory.HOLY_ARCANE,     "Deals 2d6 radiant to undead/fiends",           "25 gp",  "1 lb"),
    AdventuringGear("Arcane Focus",     GearCategory.HOLY_ARCANE,      "Crystal/orb/rod/staff/wand for spellcasting",  "varies", "varies"),
    AdventuringGear("Druidic Focus",    GearCategory.HOLY_ARCANE,      "Sprig, totem, or staff for Druid spellcasting","varies", "varies"),
    AdventuringGear("Spellbook",        GearCategory.HOLY_ARCANE,      "100 pages for wizard spells",                  "50 gp",  "3 lb"),
    // Misc
    AdventuringGear("Antitoxin (vial)", GearCategory.MISC,             "Adv on poison saves for 1 hour",               "50 gp",  "—"),
    AdventuringGear("Caltrops (bag)",   GearCategory.MISC,             "Spread 5 ft: speed halved, 1 pierce damage",   "1 gp",   "2 lb"),
    AdventuringGear("Chain (10 ft)",    GearCategory.MISC,             "Iron chain, 10 HP",                            "5 gp",   "10 lb"),
    AdventuringGear("Chalk (1 piece)",  GearCategory.MISC,             "Write on stone surfaces",                      "1 cp",   "—"),
    AdventuringGear("Crowbar",          GearCategory.MISC,             "Advantage on STR checks to open/pry",          "2 gp",   "5 lb"),
    AdventuringGear("Ink (1 oz)",       GearCategory.MISC,             "Writing ink",                                  "10 gp",  "—"),
    AdventuringGear("Map Case / Scroll Case", GearCategory.MISC,       "Holds 10 rolled sheets",                       "1 gp",   "1 lb"),
    AdventuringGear("Mirror, Steel",    GearCategory.MISC,             "Handy for checking corners and disguises",     "5 gp",   "0.5 lb"),
    AdventuringGear("Spyglass",         GearCategory.MISC,             "Magnify distant objects up to x2",             "1000 gp","1 lb"),
    AdventuringGear("Shovel",           GearCategory.MISC,             "Dig 2 cu ft per minute",                       "2 gp",   "5 lb"),
    AdventuringGear("Poison, Basic (vial)", GearCategory.MISC,         "Coat weapon: DC 10 CON save or 1d4 poison dmg", "100 gp", "—")
)

// ─── Packs (pre-made bundles) ──────────────────────────────────────────────────

data class Pack(val name: String, val contents: List<String>, val cost: String)

val STARTING_PACKS = listOf(
    Pack("Burglar's Pack",       listOf("Backpack", "1000 Ball Bearings", "Rope (silk 10 ft)", "Bell", "5 Candles", "Crowbar", "Hammer", "10 Pitons", "Hooded Lantern", "2 Oil Flasks", "5 Days Rations", "Tinderbox", "Waterskin"), "16 gp"),
    Pack("Diplomat's Pack",      listOf("Chest", "2 Map Cases", "Fine Clothes", "Ink + Pen", "Lamp", "2 Oil Flasks", "5 Sheets Paper", "Wax Seal", "Soap", "Signet Ring", "Waterskin"), "39 gp"),
    Pack("Dungeoneer's Pack",    listOf("Backpack", "Crowbar", "Hammer", "10 Pitons", "10 Torches", "Tinderbox", "10 Days Rations", "Waterskin", "Rope (hempen 50 ft)"), "12 gp"),
    Pack("Entertainer's Pack",   listOf("Backpack", "Bedroll", "2 Costumes", "5 Candles", "5 Days Rations", "Waterskin", "Disguise Kit"), "40 gp"),
    Pack("Explorer's Pack",      listOf("Backpack", "Bedroll", "Mess Kit", "Tinderbox", "10 Torches", "10 Days Rations", "Waterskin", "Rope (hempen 50 ft)"), "10 gp"),
    Pack("Priest's Pack",        listOf("Backpack", "Blanket", "10 Candles", "Tinderbox", "Alms Box", "2 Incense Blocks", "Censer", "Vestments", "2 Days Rations", "Waterskin"), "19 gp"),
    Pack("Scholar's Pack",       listOf("Backpack", "Book of Lore", "Ink Bottle", "Ink Pen", "10 Sheets Parchment", "Small Bag of Sand", "Small Knife"), "40 gp")
)
