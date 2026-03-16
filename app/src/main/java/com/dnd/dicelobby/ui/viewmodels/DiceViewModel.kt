package com.dnd.dicelobby.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnd.dicelobby.dice.DiceFormula
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.physics.PhysicsWorld
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Manages the dice-rolling UI state and bridges the physics simulation with the UI.
 *
 * This ViewModel owns the [PhysicsWorld] used by [DiceRenderer] and orchestrates
 * the animation life-cycle:
 *   1. User picks a die type + optional modifier → taps Roll.
 *   2. [startRoll] resets the world, spawns physics bodies, shows the overlay.
 *   3. [DiceRenderer] calls [onPhysicsSettled] when all dice stop moving.
 *   4. The result is forwarded to [LobbyViewModel.requestRoll] via [onRollReady].
 *   5. After a short display delay, the overlay fades out.
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

    /** Whether the 3-D animation overlay is visible. */
    private val _showAnimation = MutableStateFlow(false)
    val showAnimation: StateFlow<Boolean> = _showAnimation.asStateFlow()

    /** Whether this is a hidden (DM-only) roll. */
    private val _hiddenRoll   = MutableStateFlow(false)
    val hiddenRoll: StateFlow<Boolean> = _hiddenRoll.asStateFlow()

    /** Shared physics world updated each frame by [DiceRenderer]. */
    val physicsWorld = PhysicsWorld()

    /** Callback invoked once the physics settles; delivers formula string + hidden flag. */
    var onRollReady: ((formula: String, hidden: Boolean) -> Unit)? = null

    private var rollRandom = Random.Default

    fun selectDice(type: DiceType) { _selectedDice.value = type }
    fun setDiceCount(n: Int)       { _diceCount.value = n.coerceIn(1, 20) }
    fun setModifier(m: Int)        { _modifier.value = m }
    fun setCustomFormula(f: String){ _customFormula.value = f }
    fun setHiddenRoll(h: Boolean)  { _hiddenRoll.value = h }

    /** Build the formula string from current selections. */
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
     * Begin a new roll animation.
     * @param count  Number of dice to spawn (capped at 8 for performance).
     */
    fun startRoll(count: Int = _diceCount.value) {
        physicsWorld.clear()
        rollRandom = Random(System.currentTimeMillis())

        val cap = count.coerceIn(1, 8)
        repeat(cap) { physicsWorld.spawnDie(rollRandom) }

        _showAnimation.value = true
    }

    /**
     * Called by [DiceRenderer] (on the GL thread) once all dice have settled.
     * Posts back to the main thread via the ViewModel scope.
     */
    fun onPhysicsSettled(faceValues: List<Int>) {
        viewModelScope.launch {
            val formula = buildFormula()
            val hidden  = _hiddenRoll.value
            onRollReady?.invoke(formula, hidden)

            // Keep overlay visible for 1 second so the player can see the result
            delay(1_000)
            _showAnimation.value = false
        }
    }
}
