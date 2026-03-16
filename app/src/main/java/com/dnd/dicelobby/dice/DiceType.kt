package com.dnd.dicelobby.dice

/**
 * All standard RPG dice types, each carrying the number of faces and a short label.
 *
 * The `faces` property drives both the physics simulation face-count and the random
 * result range.  For d100 we internally use two d10s but expose `faces = 100`.
 */
enum class DiceType(val faces: Int, val label: String) {
    D4(4, "d4"),
    D6(6, "d6"),
    D8(8, "d8"),
    D10(10, "d10"),
    D12(12, "d12"),
    D20(20, "d20"),
    D100(100, "d100");

    companion object {
        /** Parse a face-count integer to the matching DiceType, or null if unknown. */
        fun fromFaces(faces: Int): DiceType? = entries.firstOrNull { it.faces == faces }

        /** Parse a lowercase label string (e.g. "d20") to the matching DiceType. */
        fun fromLabel(label: String): DiceType? =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) }
    }
}
