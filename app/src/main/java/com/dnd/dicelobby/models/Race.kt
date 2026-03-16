package com.dnd.dicelobby.models

/**
 * D&D 5e (PHB) races and their subraces.
 *
 * Races with no subraces have an empty [subraces] list.
 */
data class Race(
    val name: String,
    val subraces: List<String> = emptyList()
)

val DND_RACES = listOf(
    Race("Dragonborn"),
    Race(
        "Dwarf",
        listOf("Hill Dwarf", "Mountain Dwarf")
    ),
    Race(
        "Elf",
        listOf("High Elf", "Wood Elf", "Dark Elf (Drow)")
    ),
    Race(
        "Gnome",
        listOf("Forest Gnome", "Rock Gnome")
    ),
    Race("Half-Elf"),
    Race("Half-Orc"),
    Race(
        "Halfling",
        listOf("Lightfoot", "Stout")
    ),
    Race(
        "Human",
        listOf("Standard", "Variant")
    ),
    Race("Tiefling")
)
