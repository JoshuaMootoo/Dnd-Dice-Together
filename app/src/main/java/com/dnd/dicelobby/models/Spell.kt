package com.dnd.dicelobby.models

data class Spell(
    val name: String,
    val level: Int,          // 0 = cantrip
    val school: String,
    val castingTime: String,
    val range: String,
    val components: String,
    val duration: String,
    val description: String,
    val classes: List<String>
)

val DND_SPELLS: List<Spell> = listOf(

    // ── Cantrips (level 0) ────────────────────────────────────────────────────

    Spell("Fire Bolt", 0, "Evocation", "1 action", "120 ft", "V, S", "Instantaneous",
        "You hurl a mote of fire at a creature or object within range. Make a ranged spell attack. On a hit, the target takes 1d10 fire damage.",
        listOf("Artificer", "Sorcerer", "Wizard")),

    Spell("Eldritch Blast", 0, "Evocation", "1 action", "120 ft", "V, S", "Instantaneous",
        "A beam of crackling energy streaks toward a creature within range. Make a ranged spell attack. On a hit, the target takes 1d10 force damage.",
        listOf("Warlock")),

    Spell("Sacred Flame", 0, "Evocation", "1 action", "60 ft", "V, S", "Instantaneous",
        "Flame-like radiance descends on a creature you can see within range. The target must succeed on a Dexterity saving throw or take 1d8 radiant damage.",
        listOf("Cleric")),

    Spell("Mage Hand", 0, "Conjuration", "1 action", "30 ft", "V, S", "1 minute",
        "A spectral, floating hand appears at a point you choose within range. The hand can carry up to 10 pounds and can move up to 30 feet each turn.",
        listOf("Artificer", "Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Minor Illusion", 0, "Illusion", "1 action", "30 ft", "S, M", "1 minute",
        "You create a sound or an image of an object within range that lasts for the duration. The illusion ends if you dismiss it as an action or cast this spell again.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Prestidigitation", 0, "Transmutation", "1 action", "10 ft", "V, S", "Up to 1 hour",
        "This spell is a minor magical trick that novice spellcasters use for practice. You create a small, harmless sensory effect; instantly clean or soil an object; light or snuff a small flame; or chill, warm, or flavor material.",
        listOf("Artificer", "Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Vicious Mockery", 0, "Enchantment", "1 action", "60 ft", "V", "Instantaneous",
        "You unleash a string of insults laced with subtle enchantments at a creature within range. The creature must succeed on a Wisdom saving throw or take 1d4 psychic damage and have disadvantage on the next attack roll before the end of its next turn.",
        listOf("Bard")),

    Spell("Toll the Dead", 0, "Necromancy", "1 action", "60 ft", "V, S", "Instantaneous",
        "You point at one creature you can see within range, and the sound of a dolorous bell fills the air around it for a moment. The target must succeed on a Wisdom saving throw or take 1d8 necrotic damage (1d12 if it is missing any hit points).",
        listOf("Cleric", "Warlock", "Wizard")),

    Spell("Chill Touch", 0, "Necromancy", "1 action", "120 ft", "V, S", "1 round",
        "You create a ghostly, skeletal hand in the space of a creature within range. Make a ranged spell attack. On a hit, the target takes 1d8 necrotic damage, and it can't regain hit points until the start of your next turn.",
        listOf("Sorcerer", "Warlock", "Wizard")),

    Spell("Guidance", 0, "Divination", "1 action", "Touch", "V, S", "Concentration, up to 1 minute",
        "You touch one willing creature. Once before the spell ends, the target can roll a d4 and add the number rolled to one ability check of its choice.",
        listOf("Artificer", "Cleric", "Druid")),

    Spell("Shillelagh", 0, "Transmutation", "1 bonus action", "Touch", "V, S, M", "1 minute",
        "The wood of a club or quarterstaff you are holding is imbued with nature's power. For the duration, you can use your spellcasting ability instead of Strength for attack and damage rolls using that weapon, and the weapon's damage die becomes a d8.",
        listOf("Druid")),

    Spell("Thunderclap", 0, "Evocation", "1 action", "5 ft", "S", "Instantaneous",
        "You create a burst of thunderous sound that can be heard up to 100 feet away. Each creature other than you within 5 feet of you must succeed on a Constitution saving throw or take 1d6 thunder damage.",
        listOf("Artificer", "Bard", "Druid", "Sorcerer", "Warlock", "Wizard")),

    // ── 1st Level ─────────────────────────────────────────────────────────────

    Spell("Magic Missile", 1, "Evocation", "1 action", "120 ft", "V, S", "Instantaneous",
        "You create three glowing darts of magical force. Each dart hits a creature of your choice that you can see within range. A dart deals 1d4 + 1 force damage to its target.",
        listOf("Sorcerer", "Wizard")),

    Spell("Cure Wounds", 1, "Evocation", "1 action", "Touch", "V, S", "Instantaneous",
        "A creature you touch regains a number of hit points equal to 1d8 + your spellcasting ability modifier.",
        listOf("Artificer", "Bard", "Cleric", "Druid", "Paladin", "Ranger")),

    Spell("Shield", 1, "Abjuration", "1 reaction", "Self", "V, S", "1 round",
        "An invisible barrier of magical force appears and protects you. Until the start of your next turn, you have a +5 bonus to AC, including against the triggering attack, and you take no damage from Magic Missile.",
        listOf("Sorcerer", "Wizard")),

    Spell("Burning Hands", 1, "Evocation", "1 action", "Self (15-ft cone)", "V, S", "Instantaneous",
        "As you hold your hands with thumbs touching and fingers spread, a thin sheet of flames shoots forth. Each creature in a 15-foot cone must make a Dexterity saving throw. A creature takes 3d6 fire damage on a failed save, or half as much on a successful one.",
        listOf("Sorcerer", "Wizard")),

    Spell("Charm Person", 1, "Enchantment", "1 action", "30 ft", "V, S", "1 hour",
        "You attempt to charm a humanoid you can see within range. It must make a Wisdom saving throw, and does so with advantage if you or your companions are fighting it. If it fails the saving throw, it is charmed by you until the spell ends or you or your companions do anything harmful to it.",
        listOf("Bard", "Druid", "Sorcerer", "Warlock", "Wizard")),

    Spell("Sleep", 1, "Enchantment", "1 action", "90 ft", "V, S, M", "1 minute",
        "This spell sends creatures into a magical slumber. Roll 5d8; the total is how many hit points of creatures this spell can affect. Starting with the creature that has the lowest current hit points, each creature affected by this spell falls unconscious.",
        listOf("Bard", "Sorcerer", "Wizard")),

    Spell("Detect Magic", 1, "Divination", "1 action", "Self", "V, S", "Concentration, up to 10 minutes",
        "For the duration, you sense the presence of magic within 30 feet of you. If you sense magic in this way, you can use your action to see a faint aura around any visible creature or object in the area that bears magic.",
        listOf("Artificer", "Bard", "Cleric", "Druid", "Paladin", "Ranger", "Sorcerer", "Wizard")),

    Spell("Thunderwave", 1, "Evocation", "1 action", "Self (15-ft cube)", "V, S", "Instantaneous",
        "A wave of thunderous force sweeps out from you. Each creature in a 15-foot cube originating from you must make a Constitution saving throw. On a failed save, a creature takes 2d8 thunder damage and is pushed 10 feet away from you. On a successful save, the creature takes half as much damage and isn't pushed.",
        listOf("Bard", "Cleric", "Druid", "Sorcerer", "Wizard")),

    Spell("Healing Word", 1, "Evocation", "1 bonus action", "60 ft", "V", "Instantaneous",
        "A creature of your choice that you can see within range regains hit points equal to 1d4 + your spellcasting ability modifier.",
        listOf("Bard", "Cleric", "Druid")),

    Spell("Bless", 1, "Enchantment", "1 action", "30 ft", "V, S, M", "Concentration, up to 1 minute",
        "You bless up to three creatures of your choice within range. Whenever a target makes an attack roll or a saving throw before the spell ends, the target can roll a d4 and add the number rolled to the attack roll or saving throw.",
        listOf("Cleric", "Paladin")),

    Spell("Bane", 1, "Enchantment", "1 action", "30 ft", "V, S, M", "Concentration, up to 1 minute",
        "Up to three creatures of your choice that you can see within range must make Charisma saving throws. Whenever a target that fails this saving throw makes an attack roll or a saving throw before the spell ends, the target must roll a d4 and subtract the number rolled from the attack roll or saving throw.",
        listOf("Bard", "Cleric")),

    Spell("Hex", 1, "Enchantment", "1 bonus action", "90 ft", "V, S, M", "Concentration, up to 1 hour",
        "You place a curse on a creature that you can see within range. Until the spell ends, you deal an extra 1d6 necrotic damage to the target whenever you hit it with an attack. Also, choose one ability when you cast the spell. The target has disadvantage on ability checks made with the chosen ability.",
        listOf("Warlock")),

    Spell("Hunter's Mark", 1, "Divination", "1 bonus action", "90 ft", "V", "Concentration, up to 1 hour",
        "You choose a creature you can see within range and mystically mark it as your quarry. Until the spell ends, you deal an extra 1d6 damage to the target whenever you hit it with a weapon attack, and you have advantage on any Wisdom (Perception) or Wisdom (Survival) check you make to find it.",
        listOf("Ranger")),

    Spell("Grease", 1, "Conjuration", "1 action", "60 ft", "V, S, M", "1 minute",
        "Slick grease covers the ground in a 10-foot square centered on a point within range and turns it into difficult terrain for the duration. When the grease appears, each creature standing in its area must succeed on a Dexterity saving throw or fall prone.",
        listOf("Artificer", "Wizard")),

    Spell("Mage Armor", 1, "Abjuration", "1 action", "Touch", "V, S, M", "8 hours",
        "You touch a willing creature who isn't wearing armor, and a protective magical force surrounds it until the spell ends. The target's base AC becomes 13 + its Dexterity modifier.",
        listOf("Sorcerer", "Wizard")),

    // ── 2nd Level ─────────────────────────────────────────────────────────────

    Spell("Misty Step", 2, "Conjuration", "1 bonus action", "Self", "V", "Instantaneous",
        "Briefly surrounded by silvery mist, you teleport up to 30 feet to an unoccupied space that you can see.",
        listOf("Sorcerer", "Warlock", "Wizard")),

    Spell("Hold Person", 2, "Enchantment", "1 action", "60 ft", "V, S, M", "Concentration, up to 1 minute",
        "Choose a humanoid that you can see within range. The target must succeed on a Wisdom saving throw or be paralyzed for the duration.",
        listOf("Bard", "Cleric", "Druid", "Sorcerer", "Warlock", "Wizard")),

    Spell("Scorching Ray", 2, "Evocation", "1 action", "120 ft", "V, S", "Instantaneous",
        "You create three rays of fire and hurl them at targets within range. You can hurl them at one target or several. Make a ranged spell attack for each ray. On a hit, the target takes 2d6 fire damage.",
        listOf("Sorcerer", "Wizard")),

    Spell("Invisibility", 2, "Illusion", "1 action", "Touch", "V, S, M", "Concentration, up to 1 hour",
        "A creature you touch becomes invisible until the spell ends. Anything the target is wearing or carrying is invisible as long as it is on the target's person. The spell ends for a target that attacks or casts a spell.",
        listOf("Artificer", "Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Mirror Image", 2, "Illusion", "1 action", "Self", "V, S", "1 minute",
        "Three illusory duplicates of yourself appear in your space. Until the spell ends, the duplicates move with you and mimic your actions, shifting position so it's impossible to track which image is real.",
        listOf("Sorcerer", "Warlock", "Wizard")),

    Spell("Shatter", 2, "Evocation", "1 action", "60 ft", "V, S, M", "Instantaneous",
        "A sudden loud ringing noise, painfully intense, erupts from a point of your choice within range. Each creature in a 10-foot-radius sphere centered on that point must make a Constitution saving throw. A creature takes 3d8 thunder damage on a failed save, or half as much on a successful one.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Spiritual Weapon", 2, "Evocation", "1 bonus action", "60 ft", "V, S", "1 minute",
        "You create a floating, spectral weapon within range that lasts for the duration or until you cast this spell again. When you cast the spell, you can make a melee spell attack against a creature within 5 feet of the weapon. On a hit, the target takes force damage equal to 1d8 + your spellcasting ability modifier.",
        listOf("Cleric")),

    Spell("Moonbeam", 2, "Evocation", "1 action", "120 ft", "V, S, M", "Concentration, up to 1 minute",
        "A silvery beam of pale light shines down in a 5-foot-radius, 40-foot-high cylinder centered on a point within range. Until the spell ends, dim light fills the cylinder. When a creature enters the spell's area for the first time on a turn or starts its turn there, it is engulfed in ghostly flames that cause searing pain, and it must make a Constitution saving throw. It takes 2d10 radiant damage on a failed save, or half as much on a successful one.",
        listOf("Druid")),

    Spell("Suggestion", 2, "Enchantment", "1 action", "30 ft", "V, M", "Concentration, up to 8 hours",
        "You suggest a course of activity (limited to a sentence or two) and magically influence a creature you can see within range that can hear and understand you. Creatures that can't be charmed are immune to this effect. The suggestion must be worded in such a manner as to make the course of action sound reasonable.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Pass Without Trace", 2, "Abjuration", "1 action", "Self", "V, S, M", "Concentration, up to 1 hour",
        "A veil of shadows and silence radiates from you, masking you and your companions from detection. For the duration, each creature you choose within 30 feet of you (including you) has a +10 bonus to Dexterity (Stealth) checks and can't be tracked except by magical means.",
        listOf("Druid", "Ranger")),

    // ── 3rd Level ─────────────────────────────────────────────────────────────

    Spell("Fireball", 3, "Evocation", "1 action", "150 ft", "V, S, M", "Instantaneous",
        "A bright streak flashes from your pointing finger to a point you choose within range and then blossoms with a low roar into an explosion of flame. Each creature in a 20-foot-radius sphere centered on that point must make a Dexterity saving throw. A target takes 8d6 fire damage on a failed save, or half as much on a successful one.",
        listOf("Sorcerer", "Wizard")),

    Spell("Lightning Bolt", 3, "Evocation", "1 action", "Self (100-ft line)", "V, S, M", "Instantaneous",
        "A stroke of lightning forming a line 100 feet long and 5 feet wide blasts out from you in a direction you choose. Each creature in the line must make a Dexterity saving throw. A creature takes 8d6 lightning damage on a failed save, or half as much on a successful one.",
        listOf("Sorcerer", "Wizard")),

    Spell("Counterspell", 3, "Abjuration", "1 reaction", "60 ft", "S", "Instantaneous",
        "You attempt to interrupt a creature in the process of casting a spell. If the creature is casting a spell of 3rd level or lower, its spell fails and has no effect. If it is casting a spell of 4th level or higher, make an ability check using your spellcasting ability. The DC equals 10 + the spell's level.",
        listOf("Sorcerer", "Warlock", "Wizard")),

    Spell("Dispel Magic", 3, "Abjuration", "1 action", "120 ft", "V, S", "Instantaneous",
        "Choose one creature, object, or magical effect within range. Any spell of 3rd level or lower on the target ends. For each spell of 4th level or higher on the target, make an ability check using your spellcasting ability. The DC equals 10 + the spell's level.",
        listOf("Bard", "Cleric", "Druid", "Paladin", "Sorcerer", "Warlock", "Wizard")),

    Spell("Fly", 3, "Transmutation", "1 action", "Touch", "V, S, M", "Concentration, up to 10 minutes",
        "You touch a willing creature. The target gains a flying speed of 60 feet for the duration. When the spell ends, the target falls if it is still aloft, unless it can stop the fall.",
        listOf("Artificer", "Sorcerer", "Warlock", "Wizard")),

    Spell("Haste", 3, "Transmutation", "1 action", "30 ft", "V, S, M", "Concentration, up to 1 minute",
        "Choose a willing creature that you can see within range. Until the spell ends, the target's speed is doubled, it gains a +2 bonus to AC, it has advantage on Dexterity saving throws, and it gains an additional action on each of its turns.",
        listOf("Artificer", "Sorcerer", "Wizard")),

    Spell("Spirit Guardians", 3, "Conjuration", "1 action", "Self (15-ft radius)", "V, S, M", "Concentration, up to 10 minutes",
        "You call forth spirits to protect you. They flit around you to a distance of 15 feet for the duration. If you are good or neutral, their spectral form appears angelic or fey (your choice). If you are evil, they appear fiendish. When you cast this spell, you can designate any number of creatures you can see to be unaffected by it.",
        listOf("Cleric")),

    Spell("Animate Dead", 3, "Necromancy", "1 minute", "10 ft", "V, S, M", "Instantaneous",
        "This spell creates an undead servant. Choose a pile of bones or a corpse of a Medium or Small humanoid within range. Your spell imbues the target with a foul mimicry of life, raising it as an undead creature. The target becomes a skeleton if you chose bones or a zombie if you chose a corpse.",
        listOf("Cleric", "Wizard")),

    Spell("Hunger of Hadar", 3, "Conjuration", "1 action", "150 ft", "V, S, M", "Concentration, up to 1 minute",
        "You open a gateway to the dark between the stars, a region infested with unknown horrors. A 20-foot-radius sphere of blackness and bitter cold appears, centered on a point with range and lasting for the duration. This void is filled with a cacophony of soft whispers and slurping noises that can be heard up to 30 feet away.",
        listOf("Warlock")),

    Spell("Call Lightning", 3, "Conjuration", "1 action", "120 ft", "V, S", "Concentration, up to 10 minutes",
        "A storm cloud appears in the shape of a cylinder that is 10 feet tall with a 60-foot radius, centered on a point you can see 100 feet directly above you. When you cast the spell, choose a point you can see within range. A bolt of lightning flashes down from the cloud to that point. Each creature within 5 feet of that point must make a Dexterity saving throw, taking 3d10 lightning damage on a failed save.",
        listOf("Druid")),

    // ── 4th Level ─────────────────────────────────────────────────────────────

    Spell("Banishment", 4, "Abjuration", "1 action", "60 ft", "V, S, M", "Concentration, up to 1 minute",
        "You attempt to send one creature that you can see within range to another plane of existence. The target must succeed on a Charisma saving throw or be banished. If the spell ends before 1 minute has passed, the target reappears in the space it left or in the nearest unoccupied space if that space is occupied.",
        listOf("Cleric", "Paladin", "Sorcerer", "Warlock", "Wizard")),

    Spell("Dimension Door", 4, "Conjuration", "1 action", "500 ft", "V", "Instantaneous",
        "You teleport yourself from your current location to any other spot within range. You arrive at exactly the spot desired. It can be a place you can see, one you can visualize, or one you can describe by stating distance and direction.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Greater Invisibility", 4, "Illusion", "1 action", "Touch", "V, S", "Concentration, up to 1 minute",
        "You or a creature you touch becomes invisible until the spell ends. Anything the target is wearing or carrying is invisible as long as it is on the target's person. Unlike Invisibility, the spell doesn't end when the target attacks or casts a spell.",
        listOf("Bard", "Sorcerer", "Wizard")),

    Spell("Polymorph", 4, "Transmutation", "1 action", "60 ft", "V, S, M", "Concentration, up to 1 hour",
        "This spell transforms a creature that you can see within range into a new form. An unwilling creature must make a Wisdom saving throw to avoid the effect. The spell has no effect on a shapechanger or a creature with 0 hit points. The transformation lasts for the duration, or until the target drops to 0 hit points or dies.",
        listOf("Bard", "Druid", "Sorcerer", "Wizard")),

    Spell("Wall of Fire", 4, "Evocation", "1 action", "120 ft", "V, S, M", "Concentration, up to 1 minute",
        "You create a wall of fire on a solid surface within range. The wall can be up to 60 feet long, 20 feet high, and 1 foot thick, or a ringed wall up to 20 feet in diameter, 20 feet high, and 1 foot thick. The wall is opaque and lasts for the duration.",
        listOf("Druid", "Sorcerer", "Wizard")),

    Spell("Ice Storm", 4, "Evocation", "1 action", "300 ft", "V, S, M", "Instantaneous",
        "A hail of rock-hard ice pounds to the ground in a 20-foot-radius, 40-foot-high cylinder centered on a point within range. Each creature in the cylinder must make a Dexterity saving throw. A creature takes 2d8 bludgeoning damage and 4d6 cold damage on a failed save, or half as much on a successful one.",
        listOf("Druid", "Sorcerer", "Wizard")),

    // ── 5th Level ─────────────────────────────────────────────────────────────

    Spell("Cone of Cold", 5, "Evocation", "1 action", "Self (60-ft cone)", "V, S, M", "Instantaneous",
        "A blast of cold air erupts from your hands. Each creature in a 60-foot cone must make a Constitution saving throw. A creature takes 8d8 cold damage on a failed save, or half as much on a successful one. A creature killed by this spell becomes a frozen statue until it thaws.",
        listOf("Sorcerer", "Wizard")),

    Spell("Hold Monster", 5, "Enchantment", "1 action", "90 ft", "V, S, M", "Concentration, up to 1 minute",
        "Choose a creature that you can see within range. The target must succeed on a Wisdom saving throw or be paralyzed for the duration. This spell has no effect on undead.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Wall of Force", 5, "Evocation", "1 action", "120 ft", "V, S, M", "Concentration, up to 10 minutes",
        "An invisible wall of force springs into existence at a point you choose within range. The wall appears in any orientation you choose, as a horizontal or vertical barrier or at an angle. It can be free floating or resting on a solid surface.",
        listOf("Wizard")),

    Spell("Mass Cure Wounds", 5, "Evocation", "1 action", "60 ft", "V, S", "Instantaneous",
        "A wave of healing energy washes out from a point of your choice within range. Choose up to six creatures in a 30-foot-radius sphere centered on that point. Each target regains hit points equal to 3d8 + your spellcasting ability modifier.",
        listOf("Bard", "Cleric", "Druid")),

    Spell("Telekinesis", 5, "Transmutation", "1 action", "60 ft", "V, S", "Concentration, up to 10 minutes",
        "You gain the ability to move or manipulate creatures or objects by thought. When you cast the spell, and as your action each round for the duration, you can exert your will on one creature or object that you can see within range.",
        listOf("Sorcerer", "Wizard")),

    Spell("Cloudkill", 5, "Conjuration", "1 action", "120 ft", "V, S", "Concentration, up to 10 minutes",
        "You create a 20-foot-radius sphere of poisonous, yellow-green fog centered on a point you choose within range. The fog spreads around corners. It lasts for the duration or until strong wind disperses the fog, ending the spell. Its area is heavily obscured.",
        listOf("Sorcerer", "Wizard")),

    // ── 6th Level ─────────────────────────────────────────────────────────────

    Spell("Disintegrate", 6, "Transmutation", "1 action", "60 ft", "V, S, M", "Instantaneous",
        "A thin green ray springs from your pointing finger to a target you can see within range. The target can be a creature, an object, or a creation of magical force, such as the wall created by Wall of Force. A creature targeted by this spell must make a Dexterity saving throw. On a failed save, the target takes 10d6 + 40 force damage. The target is disintegrated if this damage leaves it with 0 hit points.",
        listOf("Sorcerer", "Wizard")),

    Spell("Chain Lightning", 6, "Evocation", "1 action", "150 ft", "V, S, M", "Instantaneous",
        "You create a bolt of lightning that arcs toward a target of your choice that you can see within range. Three bolts then leap from that target to as many as three other targets, each of which must be within 30 feet of the first target. A target can be a creature or an object and can be targeted by only one of the bolts. A target must make a Dexterity saving throw, taking 10d8 lightning damage on a failed save.",
        listOf("Sorcerer", "Wizard")),

    Spell("Mass Suggestion", 6, "Enchantment", "1 action", "60 ft", "V, M", "24 hours",
        "You suggest a course of activity (limited to a sentence or two) and magically influence up to twelve creatures of your choice that you can see within range and that can hear and understand you.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Heal", 6, "Evocation", "1 action", "60 ft", "V, S", "Instantaneous",
        "Choose a creature that you can see within range. A surge of positive energy washes through the creature, causing it to regain 70 hit points. This spell also ends blindness, deafness, and any diseases affecting the target.",
        listOf("Cleric", "Druid")),

    // ── 7th Level ─────────────────────────────────────────────────────────────

    Spell("Finger of Death", 7, "Necromancy", "1 action", "60 ft", "V, S", "Instantaneous",
        "You send negative energy coursing through a creature that you can see within range, causing it searing pain. The target must make a Constitution saving throw. It takes 7d8 + 30 necrotic damage on a failed save, or half as much on a successful one. A humanoid killed by this spell rises at the start of your next turn as a zombie that is permanently under your command.",
        listOf("Sorcerer", "Warlock", "Wizard")),

    Spell("Teleport", 7, "Conjuration", "1 action", "10 ft", "V", "Instantaneous",
        "This spell instantly transports you and up to eight willing creatures of your choice that you can see within range, or a single object that you can see within range, to a destination you select. If you are targeting an object, it must be able to fit entirely inside a 10-foot cube, and it can't be held or carried by an unwilling creature.",
        listOf("Bard", "Sorcerer", "Wizard")),

    Spell("Forcecage", 7, "Evocation", "1 action", "100 ft", "V, S, M", "1 hour",
        "An immobile, invisible, cube-shaped prison composed of magical force springs into existence around an area you choose within range. The prison can be a cage or a solid box. A creature inside the cage can't leave it by nonmagical means.",
        listOf("Bard", "Warlock", "Wizard")),

    // ── 8th Level ─────────────────────────────────────────────────────────────

    Spell("Sunburst", 8, "Evocation", "1 action", "150 ft", "V, S, M", "Instantaneous",
        "Brilliant sunlight flashes in a 60-foot radius centered on a point you choose within range. Each creature in that light must make a Constitution saving throw. On a failed save, a creature takes 12d6 radiant damage and is blinded for 1 minute. On a successful save, it takes half as much damage and isn't blinded by this spell.",
        listOf("Druid", "Sorcerer", "Wizard")),

    Spell("Power Word Stun", 8, "Enchantment", "1 action", "60 ft", "V", "Instantaneous",
        "You overwhelm the mind of a creature you can see within range, leaving it dumbfounded. If the target has 150 hit points or fewer, it is stunned. Otherwise, the spell has no effect. The stunned target must make a Constitution saving throw at the end of each of its turns.",
        listOf("Sorcerer", "Warlock", "Wizard")),

    Spell("Dominate Monster", 8, "Enchantment", "1 action", "60 ft", "V, S", "Concentration, up to 1 hour",
        "You attempt to beguile a creature that you can see within range. It must succeed on a Wisdom saving throw or be charmed by you for the duration. While the creature is charmed, you have a telepathic link with it as long as the two of you are on the same plane of existence.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    // ── 9th Level ─────────────────────────────────────────────────────────────

    Spell("Wish", 9, "Conjuration", "1 action", "Self", "V", "Instantaneous",
        "Wish is the mightiest spell a mortal creature can cast. By simply speaking aloud, you can alter the very foundations of reality in accord with your desires. The basic use of this spell is to duplicate any other spell of 8th level or lower. Alternatively, you can create one of the following effects of your choice.",
        listOf("Sorcerer", "Wizard")),

    Spell("Power Word Kill", 9, "Enchantment", "1 action", "60 ft", "V", "Instantaneous",
        "You utter a word of power that can compel one creature you can see within range to die instantly. If the creature you choose has 100 hit points or fewer, it dies. Otherwise, the spell has no effect.",
        listOf("Bard", "Sorcerer", "Warlock", "Wizard")),

    Spell("Time Stop", 9, "Transmutation", "1 action", "Self", "V", "Instantaneous",
        "You briefly stop the flow of time for everyone but yourself. No time passes for other creatures, while you take 1d4 + 1 turns in a row, during which you can use actions and move as normal.",
        listOf("Sorcerer", "Wizard")),

    Spell("Mass Heal", 9, "Evocation", "1 action", "60 ft", "V, S", "Instantaneous",
        "A flood of healing energy flows from you into injured creatures around you. You restore up to 700 hit points, divided as you choose among any number of creatures that you can see within range. Creatures healed by this spell are also cured of all diseases and any effect making them blinded or deafened.",
        listOf("Cleric")),

    Spell("True Resurrection", 9, "Necromancy", "1 hour", "Touch", "V, S, M", "Instantaneous",
        "You touch a creature that has been dead for no longer than 200 years and that died for any reason except old age. If the creature's soul is free and willing, the creature is restored to life with all its hit points.",
        listOf("Cleric", "Druid"))
)
