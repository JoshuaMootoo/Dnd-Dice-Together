package com.dnd.dicelobby.dice

/**
 * Parsed representation of a dice expression such as "2d6+3" or "4d6kh3".
 *
 * @param count     Number of dice to roll.
 * @param type      The die type (d4…d100).
 * @param modifier  Flat bonus / penalty added after summing kept dice.
 * @param keepHighest  If non-null, keep only the N highest rolls (e.g. 4d6kh3 → 3 highest of 4d6).
 * @param keepLowest   If non-null, keep only the N lowest rolls.
 * @param dropLowest   If non-null, drop the N lowest rolls before summing.
 */
data class DiceFormula(
    val count: Int,
    val type: DiceType,
    val modifier: Int = 0,
    val keepHighest: Int? = null,
    val keepLowest: Int? = null,
    val dropLowest: Int? = null
) {
    /** Human-readable canonical form of the formula. */
    val text: String get() {
        val base = "${count}${type.label}"
        val kh = if (keepHighest != null) "kh$keepHighest" else ""
        val kl = if (keepLowest != null) "kl$keepLowest" else ""
        val dl = if (dropLowest != null) "dl$dropLowest" else ""
        val mod = when {
            modifier > 0 -> "+$modifier"
            modifier < 0 -> "$modifier"
            else -> ""
        }
        return "$base$kh$kl$dl$mod"
    }

    companion object {
        /**
         * Parses a dice formula string into a [DiceFormula].
         *
         * Supported syntax examples:
         *   "d20"      → 1d20
         *   "2d6"      → 2d6
         *   "2d6+3"    → 2d6+3
         *   "4d6kh3"   → 4d6 keep highest 3
         *   "4d6dl1"   → 4d6 drop lowest 1
         *
         * Throws [IllegalArgumentException] for invalid input.
         */
        fun parse(input: String): DiceFormula {
            val raw = input.trim().lowercase().replace(" ", "")

            // Regex: (count?)d(faces)(kh/kl/dl N)?(+/-modifier)?
            val regex = Regex(
                """^(\d*)d(\d+)(kh(\d+)|kl(\d+)|dl(\d+))?([+-]\d+)?$"""
            )
            val match = regex.matchEntire(raw)
                ?: throw IllegalArgumentException("Cannot parse dice formula: '$input'")

            val groups = match.groupValues
            val count = if (groups[1].isEmpty()) 1 else groups[1].toInt()
            val faces = groups[2].toInt()
            val type = DiceType.fromFaces(faces)
                ?: throw IllegalArgumentException("Unsupported die type: d$faces")

            val keepHighest = if (groups[4].isNotEmpty()) groups[4].toInt() else null
            val keepLowest  = if (groups[5].isNotEmpty()) groups[5].toInt() else null
            val dropLowest  = if (groups[6].isNotEmpty()) groups[6].toInt() else null
            val modifier    = if (groups[7].isNotEmpty()) groups[7].toInt() else 0

            require(count in 1..20) { "Dice count must be 1–20" }

            return DiceFormula(count, type, modifier, keepHighest, keepLowest, dropLowest)
        }
    }
}
