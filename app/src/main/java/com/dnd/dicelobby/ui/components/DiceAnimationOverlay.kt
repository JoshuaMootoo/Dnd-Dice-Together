package com.dnd.dicelobby.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.ui.theme.BG3Gold
import com.dnd.dicelobby.ui.theme.BG3GoldBorder
import com.dnd.dicelobby.ui.theme.BG3SurfaceCard
import kotlinx.coroutines.delay

/**
 * Full-screen overlay that shows a dice roll result.
 *
 * Phases:
 *   1. Rolling — each die displays a rapidly cycling random number (~0.9 s).
 *   2. Reveal  — numbers snap to their final values with a pop animation.
 *      The total is shown beneath the individual results.
 *
 * @param visible     Driven by [DiceViewModel.showAnimation].
 * @param rollResults Kept dice values for this roll.
 * @param rollModifier Flat modifier (+/-).
 * @param revealed    True once the final numbers should be shown.
 * @param diceType    Die type — used to bound the random cycling range.
 */
@Composable
fun DiceAnimationOverlay(
    visible: Boolean,
    rollResults: List<Int>,
    rollModifier: Int,
    revealed: Boolean,
    diceType: DiceType
) {
    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(tween(200)),
        exit    = fadeOut(tween(400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE0A0806)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Title
                Text(
                    text       = "THE FATES DECIDE",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = BG3Gold,
                    letterSpacing = 3.sp
                )

                // Dice result grid — wrap to at most 4 per row
                val rows = rollResults.chunked(4)
                Column(
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    horizontalAlignment   = Alignment.CenterHorizontally
                ) {
                    rows.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            row.forEach { finalValue ->
                                DieResultCell(
                                    finalValue = finalValue,
                                    revealed   = revealed,
                                    diceType   = diceType
                                )
                            }
                        }
                    }
                }

                // Modifier + total
                val total = rollResults.sum() + rollModifier
                if (revealed) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (rollModifier != 0) {
                            val modSign = if (rollModifier > 0) "+" else ""
                            Text(
                                text      = "${rollResults.joinToString(" + ")} $modSign$rollModifier",
                                color     = BG3GoldBorder,
                                fontSize  = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else if (rollResults.size > 1) {
                            Text(
                                text      = rollResults.joinToString(" + "),
                                color     = BG3GoldBorder,
                                fontSize  = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (rollResults.size > 1 || rollModifier != 0) {
                            Text(
                                text       = "= $total",
                                fontSize   = 52.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = BG3Gold
                            )
                        }
                    }
                } else {
                    Text(
                        text  = "Rolling…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BG3GoldBorder
                    )
                }
            }
        }
    }
}

/**
 * Single die result cell.
 * While [revealed] is false it cycles through random values in [1..[diceType.faces]].
 * When [revealed] becomes true it snaps to [finalValue] with a brief pop scale animation.
 */
@Composable
private fun DieResultCell(
    finalValue: Int,
    revealed: Boolean,
    diceType: DiceType
) {
    var displayValue by remember { mutableStateOf(finalValue) }

    // Cycling random numbers during rolling phase; stops when revealed
    LaunchedEffect(revealed) {
        if (!revealed) {
            while (true) {
                displayValue = (1..diceType.faces).random()
                delay(80)
            }
        } else {
            displayValue = finalValue
        }
    }

    // Pop scale: 1f → 1.25f → 1f when revealed
    var popped by remember { mutableStateOf(false) }
    LaunchedEffect(revealed) {
        if (revealed) {
            popped = true
            delay(150)
            popped = false
        }
    }
    val scale by animateFloatAsState(
        targetValue    = if (popped) 1.25f else 1f,
        animationSpec  = tween(150),
        label          = "dieScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .size(72.dp)
            .background(BG3SurfaceCard, RoundedCornerShape(8.dp))
            .border(1.5.dp, if (revealed) BG3Gold else BG3GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = displayValue.toString(),
            fontSize   = if (displayValue >= 100) 22.sp else if (displayValue >= 10) 28.sp else 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = if (revealed) BG3Gold else BG3GoldBorder.copy(alpha = 0.6f)
        )
    }
}
