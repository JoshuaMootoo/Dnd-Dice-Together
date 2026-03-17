package com.dnd.dicelobby.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnd.dicelobby.dice.DiceFormula
import com.dnd.dicelobby.dice.DiceRoller
import com.dnd.dicelobby.dice.DiceType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages the dice-rolling UI state.
 *
 * Animation life-cycle:
 *   1. User picks a die type + optional modifier → taps Roll.
 *   2. [startRoll] immediately evaluates the roll via [DiceRoller], stores the results,
 *      and shows the number-reveal overlay.
 *   3. After a short "rolling" phase the numbers are revealed on screen.
 *   4. [onRollReady] is invoked with the formula + face values so [LobbyViewModel] can
 *      broadcast the result to all players.
 *   5. After a display pause the overlay fades out.
 */
class DiceViewModel : ViewModel() {

    /** Selected die type from the quick-pick bar. */
    private val _selectedDice = MutableStateFlow(DiceType.D20)
    val selectedDice: StateFlow<DiceType> = _selectedDice.asStateFlow()

    /** Dice count (1–20). */
    private val _diceCount    = MutableStateFlow(1)
    val diceCount: StateFlow<Int> = _diceCount.asStateFlow()

    /** Flat modifier typed by the user. */
    private val _modifier     = MutableStateFlow(0)
    val modifier: StateFlow<Int> = _modifier.asStateFlow()

    /** Custom formula typed directly (overrides quick-pick when non-empty). */
    private val _customFormula = MutableStateFlow("")
    val customFormula: StateFlow<String> = _customFormula.asStateFlow()

    /** Whether the roll-result overlay is visible. */
    private val _showAnimation = MutableStateFlow(false)
    val showAnimation: StateFlow<Boolean> = _showAnimation.asStateFlow()

    /** Whether the final numbers have been revealed (false = still "rolling"). */
    private val _rollRevealed = MutableStateFlow(false)
    val rollRevealed: StateFlow<Boolean> = _rollRevealed.asStateFlow()

    /** Kept dice results for the current roll (empty between rolls). */
    private val _rollResults = MutableStateFlow<List<Int>>(emptyList())
    val rollResults: StateFlow<List<Int>> = _rollResults.asStateFlow()

    /** Active modifier value for display (+/- flat bonus). */
    private val _rollModifier = MutableStateFlow(0)
    val rollModifier: StateFlow<Int> = _rollModifier.asStateFlow()

    /** Die type used in the current roll (drives the face-count for the rolling animation). */
    private val _rollDiceType = MutableStateFlow(DiceType.D20)
    val rollDiceType: StateFlow<DiceType> = _rollDiceType.asStateFlow()

    /** Whether this is a hidden (DM-only) roll. */
    private val _hiddenRoll   = MutableStateFlow(false)
    val hiddenRoll: StateFlow<Boolean> = _hiddenRoll.asStateFlow()

    /** Callback invoked once results are ready to broadcast. */
    var onRollReady: ((formula: String, hidden: Boolean, faceValues: List<Int>) -> Unit)? = null

    fun selectDice(type: DiceType) { _selectedDice.value = type }
    fun setDiceCount(n: Int)       { _diceCount.value = n.coerceIn(1, 20) }
    fun setModifier(m: Int)        { _modifier.value = m }
    fun setCustomFormula(f: String){ _customFormula.value = f }
    fun setHiddenRoll(h: Boolean)  { _hiddenRoll.value = h }

    fun buildFormula(): String {
        if (_customFormula.value.isNotBlank()) return _customFormula.value.trim()
        val base = "${_diceCount.value}${_selectedDice.value.label}"
        val mod  = _modifier.value
        return when {
            mod > 0 -> "$base+$mod"
            mod < 0 -> "$base$mod"
            else    -> base
        }
    }

    /**
     * Immediately evaluate the roll, show the number-reveal overlay, then broadcast
     * the result after the display delay.
     */
    fun startRoll(count: Int = _diceCount.value) {
        val formula = buildFormula()
        val diceFormula = try {
            DiceFormula.parse(formula)
        } catch (e: Exception) {
            return
        }

        val result = DiceRoller.roll(diceFormula)

        _rollDiceType.value  = diceFormula.type
        _rollResults.value   = result.keptRolls
        _rollModifier.value  = result.modifier
        _rollRevealed.value  = false
        _showAnimation.value = true

        viewModelScope.launch {
            delay(900)                           // "rolling" phase — numbers spin
            _rollRevealed.value = true
            onRollReady?.invoke(formula, _hiddenRoll.value, result.keptRolls)
            delay(2_200)                         // display phase — numbers stay visible
            _showAnimation.value = false
            _rollRevealed.value  = false
        }
    }
}
