package com.dnd.dicelobby.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dnd.dicelobby.models.Roll

/**
 * Room entity persisting a single dice roll to the local SQLite database.
 * The [diceResults] list is stored as a comma-separated string.
 */
@Entity(tableName = "rolls")
data class RollEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val playerName: String,
    val diceFormula: String,
    val diceResultsCsv: String,   // "4,3,6" etc.
    val modifier: Int,
    val total: Int,
    val isHidden: Boolean,
    val timestamp: Long
) {
    /** Convert to the domain [Roll] model. */
    fun toRoll() = Roll(
        id          = id,
        playerId    = playerId,
        playerName  = playerName,
        diceFormula = diceFormula,
        diceResults = diceResultsCsv.split(",").mapNotNull { it.toIntOrNull() },
        modifier    = modifier,
        total       = total,
        isHidden    = isHidden,
        timestamp   = timestamp
    )

    companion object {
        /** Convert a domain [Roll] to a [RollEntity] for persistence. */
        fun fromRoll(roll: Roll) = RollEntity(
            id             = roll.id,
            playerId       = roll.playerId,
            playerName     = roll.playerName,
            diceFormula    = roll.diceFormula,
            diceResultsCsv = roll.diceResults.joinToString(","),
            modifier       = roll.modifier,
            total          = roll.total,
            isHidden       = roll.isHidden,
            timestamp      = roll.timestamp
        )
    }
}
