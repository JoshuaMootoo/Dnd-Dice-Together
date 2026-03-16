package com.dnd.dicelobby.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.physics.PhysicsWorld
import com.dnd.dicelobby.rendering.DiceGLView
import com.dnd.dicelobby.rendering.DiceRenderer
import com.dnd.dicelobby.ui.theme.BG3Gold
import com.dnd.dicelobby.ui.theme.BG3GoldBorder

/**
 * Full-screen overlay that displays the OpenGL dice animation.
 *
 * When [visible] becomes true, the [GLSurfaceView] is created and rendering begins.
 * Once [PhysicsWorld.allSettled] is true, the renderer calls [onSettled] and the
 * overlay fades out automatically (the caller controls [visible] in response).
 *
 * @param visible       Drive from [DiceViewModel.showAnimation].
 * @param physicsWorld  The shared [PhysicsWorld] owned by [DiceViewModel].
 * @param diceType      Type of die currently being rolled (drives mesh + face detection).
 * @param playerColor   Hex string e.g. "#E53935" — used to tint the dice.
 * @param onSettled     Called once when all dice have stopped; delivers face values.
 */
@Composable
fun DiceAnimationOverlay(
    visible: Boolean,
    physicsWorld: PhysicsWorld,
    diceType: DiceType,
    playerColor: String,
    onSettled: (List<Int>) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(),
        exit    = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000)),   // semi-transparent scrim
            contentAlignment = Alignment.Center
        ) {
            // OpenGL surface takes up the top two-thirds of the overlay
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f),
                factory = { context ->
                    val colorInt = try {
                        AndroidColor.parseColor(playerColor)
                    } catch (e: Exception) {
                        AndroidColor.RED
                    }
                    val renderer = DiceRenderer(
                        physicsWorld = physicsWorld,
                        diceType     = diceType,
                        playerColor  = colorInt,
                        onSettled    = onSettled
                    )
                    DiceGLView(context, renderer)
                }
            )

            // BG3-style "THE FATES DECIDE" text at the bottom of the overlay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text  = "THE FATES DECIDE",
                    style = MaterialTheme.typography.titleLarge,
                    color = BG3Gold
                )
                Text(
                    text  = "Rolling…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BG3GoldBorder
                )
            }
        }
    }
}
