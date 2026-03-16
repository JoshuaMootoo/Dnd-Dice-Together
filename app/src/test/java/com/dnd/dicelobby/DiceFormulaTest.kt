package com.dnd.dicelobby

import com.dnd.dicelobby.dice.DiceFormula
import com.dnd.dicelobby.dice.DiceRoller
import com.dnd.dicelobby.dice.DiceType
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for the dice formula parser and roller.
 */
class DiceFormulaTest {

    @Test
    fun `parse simple d20`() {
        val f = DiceFormula.parse("d20")
        assertEquals(1, f.count)
        assertEquals(DiceType.D20, f.type)
        assertEquals(0, f.modifier)
    }

    @Test
    fun `parse 2d6 plus 3`() {
        val f = DiceFormula.parse("2d6+3")
        assertEquals(2, f.count)
        assertEquals(DiceType.D6, f.type)
        assertEquals(3, f.modifier)
    }

    @Test
    fun `parse 4d6kh3`() {
        val f = DiceFormula.parse("4d6kh3")
        assertEquals(4, f.count)
        assertEquals(3, f.keepHighest)
    }

    @Test
    fun `roll total is within valid range`() {
        val formula = DiceFormula.parse("3d6+2")
        val rng = Random(42)
        val result = DiceRoller.roll(formula, rng)
        assertTrue(result.total in 5..20)  // min: 3×1+2=5, max: 3×6+2=20
    }

    @Test
    fun `keep highest keeps only top N dice`() {
        val formula = DiceFormula.parse("4d6kh3")
        val result  = DiceRoller.roll(formula, Random(1))
        assertEquals(4, result.rolls.size)
        assertEquals(3, result.keptRolls.size)
        // Kept rolls should be the top 3
        val top3 = result.rolls.sortedDescending().take(3)
        assertEquals(top3, result.keptRolls)
    }

    @Test
    fun `d100 result in 1 to 100`() {
        val formula = DiceFormula.parse("d100")
        repeat(20) {
            val r = DiceRoller.roll(formula)
            assertTrue("d100 result ${r.total} out of range", r.total in 1..100)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `parse invalid formula throws`() {
        DiceFormula.parse("xyz")
    }
}
