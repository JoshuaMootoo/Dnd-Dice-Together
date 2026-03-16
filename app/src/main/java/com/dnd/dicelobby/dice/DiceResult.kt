package com.dnd.dicelobby.dice

/**
 * Result of evaluating a [DiceFormula].
 *
 * @param formula   The original formula that produced this result.
 * @param rolls     Raw values for every die that was rolled (before any drop/keep filtering).
 * @param keptRolls Subset of [rolls] that were actually summed (after drop/keep).
 * @param modifier  Flat modifier applied on top of the kept dice sum.
 * @param total     Final computed total.
 */
data class DiceResult(
    val formula: DiceFormula,
    val rolls: List<Int>,
    val keptRolls: List<Int>,
    val modifier: Int,
    val total: Int
)
