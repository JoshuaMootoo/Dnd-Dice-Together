package com.dnd.dicelobby.models

/**
 * A D&D 5e race with its subraces and core traits.
 * Data sourced from rpgbot.net and the D&D 5e SRD.
 */
data class Race(
    val name: String,
    val size: String,
    val speed: Int,
    val abilityBonuses: Map<String, Int>,      // e.g. "STR" -> 2
    val traits: List<String>,
    val languages: List<String>,
    val subraces: List<Subrace> = emptyList()
)

data class Subrace(
    val name: String,
    val abilityBonuses: Map<String, Int> = emptyMap(),
    val traits: List<String> = emptyList()
)

val DND_RACES = listOf(

    Race(
        name = "Dwarf",
        size = "Medium", speed = 25,
        abilityBonuses = mapOf("CON" to 2),
        traits = listOf("Darkvision 60 ft", "Dwarven Resilience (poison)", "Dwarven Combat Training", "Stonecunning", "Tool Proficiency"),
        languages = listOf("Common", "Dwarvish"),
        subraces = listOf(
            Subrace("Hill Dwarf", mapOf("WIS" to 1), listOf("Dwarven Toughness (+1 HP per level)")),
            Subrace("Mountain Dwarf", mapOf("STR" to 2), listOf("Dwarven Armor Training (light & medium armor)")),
            Subrace("Duergar", mapOf("STR" to 1), listOf("Superior Darkvision 120 ft", "Duergar Magic (Enlarge/Reduce, Invisibility)", "Sunlight Sensitivity"))
        )
    ),

    Race(
        name = "Elf",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("DEX" to 2),
        traits = listOf("Darkvision 60 ft", "Fey Ancestry (charm immunity adv)", "Trance (4-hr rest)", "Keen Senses (Perception proficiency)"),
        languages = listOf("Common", "Elvish"),
        subraces = listOf(
            Subrace("High Elf", mapOf("INT" to 1), listOf("Extra Wizard Cantrip", "Elf Weapon Training", "Extra Language")),
            Subrace("Wood Elf", mapOf("WIS" to 1), listOf("Elf Weapon Training", "Fleet of Foot (speed 35)", "Mask of the Wild")),
            Subrace("Dark Elf (Drow)", mapOf("CHA" to 1), listOf("Superior Darkvision 120 ft", "Sunlight Sensitivity", "Drow Magic (Dancing Lights, Faerie Fire, Darkness)", "Drow Weapon Training")),
            Subrace("Sea Elf", mapOf("CON" to 1), listOf("Child of the Sea (swim 30, breathe water)", "Friend of the Sea", "Elf Weapon Training")),
            Subrace("Eladrin", mapOf("INT" to 1), listOf("Fey Step (teleport 30 ft, short/long rest)")),
            Subrace("Shadar-kai", mapOf("CON" to 1), listOf("Blessing of the Raven Queen (teleport + resistance, short rest)")),
            Subrace("Pallid Elf", mapOf("WIS" to 1), listOf("Incisive Sense (adv Insight/Investigation)", "Blessing of the Moon Weaver (Invisibility innate)"))
        )
    ),

    Race(
        name = "Gnome",
        size = "Small", speed = 25,
        abilityBonuses = mapOf("INT" to 2),
        traits = listOf("Darkvision 60 ft", "Gnome Cunning (adv all INT/WIS/CHA saves vs magic)"),
        languages = listOf("Common", "Gnomish"),
        subraces = listOf(
            Subrace("Forest Gnome", mapOf("DEX" to 1), listOf("Natural Illusionist (Minor Illusion cantrip)", "Speak with Small Beasts")),
            Subrace("Rock Gnome", mapOf("CON" to 1), listOf("Artificer's Lore (+2 prof History for magic items)", "Tinker (craft tiny clockwork devices)")),
            Subrace("Deep Gnome (Svirfneblin)", mapOf("DEX" to 1), listOf("Superior Darkvision 120 ft", "Stone Camouflage (adv Stealth in rocky terrain)", "Extra Language: Undercommon"))
        )
    ),

    Race(
        name = "Halfling",
        size = "Small", speed = 25,
        abilityBonuses = mapOf("DEX" to 2),
        traits = listOf("Lucky (reroll 1s on d20)", "Brave (adv saves vs frightened)", "Halfling Nimbleness (move through larger creatures' spaces)"),
        languages = listOf("Common", "Halfling"),
        subraces = listOf(
            Subrace("Lightfoot", mapOf("CHA" to 1), listOf("Naturally Stealthy (hide behind larger creatures)")),
            Subrace("Stout", mapOf("CON" to 1), listOf("Stout Resilience (adv saves vs poison, resistance to poison damage)")),
            Subrace("Ghostwise", mapOf("WIS" to 1), listOf("Silent Speech (telepathy 30 ft, willing creatures)")),
            Subrace("Lotusden", mapOf("WIS" to 1), listOf("Children of the Woods (Druidcraft, Entangle, Spike Growth innate)", "Timberwalk (difficult terrain from plants: no extra movement)"))
        )
    ),

    Race(
        name = "Human",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 1, "DEX" to 1, "CON" to 1, "INT" to 1, "WIS" to 1, "CHA" to 1),
        traits = listOf("Extra Language"),
        languages = listOf("Common", "One extra language"),
        subraces = listOf(
            Subrace("Standard Human", emptyMap(), listOf("+1 to all six ability scores", "Extra Language")),
            Subrace("Variant Human", mapOf("Any" to 1, "Any2" to 1), listOf("+1 to two chosen ability scores", "One skill proficiency", "One feat at 1st level"))
        )
    ),

    Race(
        name = "Dragonborn",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 2, "CHA" to 1),
        traits = listOf("Draconic Ancestry (choose dragon type)", "Breath Weapon (area damage, uses = prof bonus)", "Damage Resistance (matching ancestry type)"),
        languages = listOf("Common", "Draconic"),
        subraces = listOf(
            Subrace("Chromatic Dragonborn", emptyMap(), listOf("Chromatic Ancestry (Acid/Cold/Fire/Lightning/Poison)", "Line or Cone breath weapon", "Chromatic Warding (immunity at 5th level)")),
            Subrace("Metallic Dragonborn", emptyMap(), listOf("Metallic Ancestry (Brass/Bronze/Copper/Gold/Silver)", "Cone breath weapon", "Metallic Breath Weapon (secondary effect: incapacitate or push)")),
            Subrace("Gem Dragonborn", emptyMap(), listOf("Gem Ancestry (Amethyst/Crystal/Emerald/Sapphire/Topaz)", "Cone breath weapon (unique damage types)", "Psionic Mind (telepathy)", "Gem Flight (fly speed 1 min, prof bonus times/day)"))
        )
    ),

    Race(
        name = "Half-Elf",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("CHA" to 2, "Any" to 1, "Any2" to 1),
        traits = listOf("Darkvision 60 ft", "Fey Ancestry (adv vs charm, immunity to magical sleep)", "Skill Versatility (2 skill proficiencies)"),
        languages = listOf("Common", "Elvish", "One extra language")
    ),

    Race(
        name = "Half-Orc",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 2, "CON" to 1),
        traits = listOf("Darkvision 60 ft", "Menacing (Intimidation proficiency)", "Relentless Endurance (drop to 1 HP instead of 0, once per long rest)", "Savage Attacks (+1 die on crit with melee weapon)"),
        languages = listOf("Common", "Orc")
    ),

    Race(
        name = "Tiefling",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("INT" to 1, "CHA" to 2),
        traits = listOf("Darkvision 60 ft", "Hellish Resistance (fire damage resistance)", "Infernal Legacy (Thaumaturgy / Hellish Rebuke / Darkness innate spells)"),
        languages = listOf("Common", "Infernal"),
        subraces = listOf(
            Subrace("Asmodeus", mapOf("INT" to 1, "CHA" to 2), listOf("Infernal Legacy: Thaumaturgy, Hellish Rebuke, Darkness")),
            Subrace("Zariel", mapOf("STR" to 1, "CHA" to 2), listOf("Martial Legacy: Thaumaturgy, Searing Smite, Branding Smite")),
            Subrace("Glasya", mapOf("DEX" to 1, "CHA" to 2), listOf("Legacy of Malbolge: Minor Illusion, Disguise Self, Invisibility")),
            Subrace("Levistus", mapOf("CON" to 1, "CHA" to 2), listOf("Legacy of Stygia: Ray of Frost, Armor of Agathys, Darkness")),
            Subrace("Dispater", mapOf("DEX" to 1, "CHA" to 2), listOf("Legacy of Dis: Thaumaturgy, Disguise Self, Detect Thoughts")),
            Subrace("Feral (SCAG)", mapOf("DEX" to 1, "INT" to 1), listOf("Darkvision 60 ft", "Hellish Resistance", "Devil's Tongue or Winged variant")),
            Subrace("Winged", emptyMap(), listOf("Fly speed 30 ft (replaces Infernal Legacy)"))
        )
    ),

    Race(
        name = "Aasimar",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("CHA" to 2),
        traits = listOf("Darkvision 60 ft", "Celestial Resistance (necrotic & radiant resistance)", "Healing Hands (heal HP = prof bonus, once/long rest)", "Light Bearer (Light cantrip)"),
        languages = listOf("Common", "Celestial"),
        subraces = listOf(
            Subrace("Protector Aasimar", mapOf("WIS" to 1), listOf("Radiant Soul (fly speed 30 ft + radiant damage, 1 min/long rest)")),
            Subrace("Scourge Aasimar", mapOf("CON" to 1), listOf("Radiant Consumption (radiant damage aura, 1 min/long rest)")),
            Subrace("Fallen Aasimar", mapOf("STR" to 1), listOf("Necrotic Shroud (frighten enemies + necrotic damage, 1 min/long rest)"))
        )
    ),

    Race(
        name = "Genasi",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("CON" to 2),
        traits = listOf("Medium size", "Speed 30 ft"),
        languages = listOf("Common", "Primordial"),
        subraces = listOf(
            Subrace("Air Genasi", mapOf("DEX" to 1), listOf("Unending Breath (hold breath indefinitely)", "Mingle with the Wind (Levitate 1/long rest)")),
            Subrace("Earth Genasi", mapOf("STR" to 1), listOf("Earth Walk (move on difficult terrain: earth/stone no extra movement)", "Merge with Stone (Pass Without Trace 1/long rest)")),
            Subrace("Fire Genasi", mapOf("INT" to 1), listOf("Darkvision 60 ft", "Fire Resistance", "Reach to the Blaze (Produce Flame cantrip, Burning Hands 1/long rest)")),
            Subrace("Water Genasi", mapOf("WIS" to 1), listOf("Acid Resistance", "Amphibious (breathe air & water)", "Swim speed 30 ft", "Call to the Wave (Shape Water cantrip, Create/Destroy Water 1/long rest)"))
        )
    ),

    Race(
        name = "Goliath",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 2, "CON" to 1),
        traits = listOf("Natural Athlete (Athletics proficiency)", "Stone's Endurance (reaction: reduce damage by 1d12+CON, once/short rest)", "Powerful Build (count as Large for carry weight)", "Mountain Born (cold resistance, high altitude acclimated)"),
        languages = listOf("Common", "Giant")
    ),

    Race(
        name = "Firbolg",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("WIS" to 2, "STR" to 1),
        traits = listOf("Firbolg Magic (Detect Magic & Disguise Self 1/short rest)", "Hidden Step (bonus action invisible until attack/cast/end of turn, 1/short rest)", "Powerful Build (count as Large for carry weight)", "Speech of Beast and Leaf (communicate with beasts/plants)"),
        languages = listOf("Common", "Elvish", "Giant")
    ),

    Race(
        name = "Kenku",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("DEX" to 2, "WIS" to 1),
        traits = listOf("Expert Forgery (adv copying documents/objects)", "Kenku Training (2 proficiencies: Acrobatics/Deception/Stealth/Sleight of Hand)", "Mimicry (mimic sounds/voices heard, Insight contested to detect)"),
        languages = listOf("Common", "Auran (understand only, speak via mimicry)")
    ),

    Race(
        name = "Lizardfolk",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("CON" to 2, "WIS" to 1),
        traits = listOf("Bite (1d6+STR natural weapon)", "Cunning Artisan (craft items from slain creatures on short rest)", "Hold Breath (15 minutes)", "Hunter's Lore (2 proficiencies: Animal Handling/Nature/Perception/Stealth/Survival)", "Natural Armor (AC 13+DEX, ignore armor)", "Hungry Jaws (bonus attack + temp HP, 1/short rest)", "Swim speed 30 ft"),
        languages = listOf("Common", "Draconic")
    ),

    Race(
        name = "Tabaxi",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("DEX" to 2, "CHA" to 1),
        traits = listOf("Darkvision 60 ft", "Feline Agility (double speed until turn not moving)", "Cat's Claws (climb speed 20 ft, 1d4 slashing natural weapon)", "Cat's Talent (Perception & Stealth proficiency)"),
        languages = listOf("Common", "One extra language")
    ),

    Race(
        name = "Triton",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 1, "CON" to 1, "CHA" to 1),
        traits = listOf("Amphibious (breathe air & water)", "Control Air and Water (Fog Cloud, Gust of Wind, Wall of Water innate)", "Darkvision 60 ft", "Emissary of the Sea (communicate with sea creatures)", "Guardians of the Depths (cold resistance, no pressure penalties)", "Swim speed 30 ft"),
        languages = listOf("Common", "Primordial")
    ),

    Race(
        name = "Yuan-ti Pureblood",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("INT" to 1, "CHA" to 2),
        traits = listOf("Darkvision 60 ft", "Innate Spellcasting (Animal Friendship unlimited, Poison Spray cantrip, Suggestion 1/long rest)", "Magic Resistance (adv saves vs spells & magical effects)", "Poison Immunity"),
        languages = listOf("Common", "Abyssal", "Draconic")
    ),

    Race(
        name = "Kobold",
        size = "Small", speed = 30,
        abilityBonuses = mapOf("DEX" to 2),
        traits = listOf("Darkvision 60 ft", "Draconic Cry (bonus action: nearby enemies get disadvantage vs your allies, 1/short rest)", "Dragon Mind (CHA +1, adv Persuasion/Deception vs draconic creatures)", "Kobold Legacy (choose one: Cunning Instinct or Defiant or Draconic Sorcery)"),
        languages = listOf("Common", "Draconic")
    ),

    Race(
        name = "Goblin",
        size = "Small", speed = 30,
        abilityBonuses = mapOf("DEX" to 2, "CON" to 1),
        traits = listOf("Darkvision 60 ft", "Fury of the Small (extra damage = level once per short rest when hitting larger creature)", "Nimble Escape (Disengage or Hide as bonus action)"),
        languages = listOf("Common", "Goblin")
    ),

    Race(
        name = "Hobgoblin",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("CON" to 2, "INT" to 1),
        traits = listOf("Darkvision 60 ft", "Martial Training (2 martial weapons + light armor prof)", "Saving Face (add bonus d4 to failed roll equal to allies within 30 ft, 1/short rest)"),
        languages = listOf("Common", "Goblin")
    ),

    Race(
        name = "Bugbear",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 2, "DEX" to 1),
        traits = listOf("Darkvision 60 ft", "Long-Limbed (reach +5 ft on opportunity attacks on your turn)", "Powerful Build (count as Large for carry weight)", "Sneaky (Stealth proficiency)", "Surprise Attack (+2d6 damage on first hit if target surprised)"),
        languages = listOf("Common", "Goblin")
    ),

    Race(
        name = "Orc",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 2, "CON" to 1),
        traits = listOf("Adrenaline Rush (Dash as bonus action, gain temp HP = CON, uses = prof bonus/long rest)", "Darkvision 60 ft", "Powerful Build (count as Large for carry weight)", "Relentless Endurance (drop to 1 HP instead of 0, once/long rest)"),
        languages = listOf("Common", "Orc")
    ),

    Race(
        name = "Harengon",
        size = "Small or Medium", speed = 30,
        abilityBonuses = mapOf("Any" to 2, "Any2" to 1),
        traits = listOf("Hare Trigger (add prof bonus to Initiative)", "Leporine Senses (Perception proficiency)", "Lucky Footwork (reaction: add d4 to failed DEX save, 1/long rest)", "Rabbit Hop (bonus action jump = prof bonus × 5 ft, uses = prof bonus/long rest)"),
        languages = listOf("Common", "One extra language")
    ),

    Race(
        name = "Fairy",
        size = "Small", speed = 30,
        abilityBonuses = mapOf("Any" to 2, "Any2" to 1),
        traits = listOf("Fairy Magic (Druidcraft cantrip, Faerie Fire 1/long rest, Enlarge/Reduce 1/long rest)", "Flight (fly speed = walking speed, light/no armor only)", "Naturally Stealthy (Stealth proficiency)"),
        languages = listOf("Common", "Sylvan")
    ),

    Race(
        name = "Centaur",
        size = "Medium", speed = 40,
        abilityBonuses = mapOf("STR" to 2, "WIS" to 1),
        traits = listOf("Charge (extra 1d6 damage if moved 30 ft before attacking, 1/turn)", "Equine Build (carry weight: Large size, can't use ladders/climbing equipment easily)", "Hooves (1d4+STR bludgeoning natural weapon)", "Natural Affinity (one Druid/Nature skill proficiency)"),
        languages = listOf("Common", "Sylvan")
    ),

    Race(
        name = "Minotaur",
        size = "Medium", speed = 30,
        abilityBonuses = mapOf("STR" to 2, "CON" to 1),
        traits = listOf("Horns (1d6+STR piercing natural weapon)", "Goring Rush (Dash + Horns attack as bonus action)", "Hammering Horns (shove after hitting with Horns, no action required)", "Imposing Presence (Intimidation or Persuasion proficiency)"),
        languages = listOf("Common", "Minotaur")
    ),

    Race(
        name = "Leonin",
        size = "Medium", speed = 35,
        abilityBonuses = mapOf("CON" to 2, "STR" to 1),
        traits = listOf("Claws (1d4+STR slashing natural weapon)", "Darkvision 60 ft", "Hunter's Instincts (Perception/Stealth/Survival/Athletics proficiency)", "Daunting Roar (bonus action frighten nearby creatures, 1/short rest)"),
        languages = listOf("Common", "Leonin")
    )
)
