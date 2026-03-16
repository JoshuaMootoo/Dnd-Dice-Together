package com.dnd.dicelobby.models

import kotlinx.serialization.Serializable

/**
 * Immutable record of a single dice roll event.
 *
 * @param id           Unique roll ID (UUID).
 * @param playerId     ID of the player who requested the roll.
 * @param playerName   Display name of the rolling player.
 * @param diceFormula  Human-readable formula, e.g. "2d6+3" or "4d6kh3".
 * @param diceResults  Individual die outcomes in rolling order.
 * @param modifier     Flat numeric modifier added to the sum (positive or negative).
 * @param total        Final computed total (sum of kept dice + modifier).
 * @param isHidden     True when a DM roll has not yet been revealed to the lobby.
 * @param timestamp    Unix epoch milliseconds when the roll was created.
 */
@Serializable
data class Roll(
    val id: String,
    val playerId: String,
    val playerName: String,
    val diceFormula: String,
    val diceResults: List<Int>,
    val modifier: Int = 0,
    val total: Int,
    val isHidden: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    /** Returns a display string for this roll appropriate to its visibility state. */
    fun displayText(viewerIsDM: Boolean): String = when {
        isHidden && !viewerIsDM -> "$playerName rolled (hidden)"
        else -> "$playerName rolled $diceFormula → $total"
    }

    /** Detail breakdown shown in the expanded log entry (e.g. "[4, 5, 3] + 2 = 12"). */
    fun detailText(): String {
        val rolls = diceResults.joinToString(", ", "[", "]")
        return if (modifier != 0) {
            val sign = if (modifier > 0) "+" else ""
            "$rolls $sign$modifier = $total"
        } else {
            "$rolls = $total"
        }
    }
}
