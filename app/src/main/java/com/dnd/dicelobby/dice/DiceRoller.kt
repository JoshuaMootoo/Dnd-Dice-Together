package com.dnd.dicelobby.dice

import kotlin.random.Random

/**
 * Evaluates a [DiceFormula] and returns a [DiceResult].
 *
 * NOTE: The host device is the sole authority for dice results.  Clients send
 * [RollRequest] messages; the host calls this roller, then broadcasts [RollResult]
 * to all players — preventing any client-side manipulation.
 *
 * The random seed is supplied by [PhysicsWorld] so that the visual simulation
 * outcome matches the transmitted result exactly (same seed → same top-face).
 */
object DiceRoller {

    /**
     * Roll all dice in [formula], apply keep/drop rules, and return the result.
     *
     * @param formula The parsed dice expression.
     * @param random  Optional [Random] instance (pass a seeded instance to make results
     *                reproducible for animation synchronisation).
     */
    fun roll(formula: DiceFormula, random: Random = Random.Default): DiceResult {
        // Special case: d100 = percentile — two d10s (tens + units)
        val rolls: List<Int> = if (formula.type == DiceType.D100) {
            List(formula.count) {
                val tens  = random.nextInt(10) * 10   // 0, 10, 20 … 90
                val units = random.nextInt(10)         // 0 … 9
                val result = tens + units
                if (result == 0) 100 else result       // "00" on both = 100
            }
        } else {
            List(formula.count) { random.nextInt(formula.type.faces) + 1 }
        }

        // Apply keep-highest / keep-lowest / drop-lowest filtering
        val keptRolls: List<Int> = when {
            formula.keepHighest != null -> rolls.sortedDescending().take(formula.keepHighest)
            formula.keepLowest  != null -> rolls.sorted().take(formula.keepLowest)
            formula.dropLowest  != null -> rolls.sortedDescending().dropLast(formula.dropLowest)
            else -> rolls
        }

        val total = keptRolls.sum() + formula.modifier

        return DiceResult(
            formula   = formula,
            rolls     = rolls,
            keptRolls = keptRolls,
            modifier  = formula.modifier,
            total     = total
        )
    }
}
