package com.dnd.dicelobby.rendering

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * Thin [GLSurfaceView] subclass that hosts the [DiceRenderer].
 * Created in code (not XML) and embedded into the Compose hierarchy via
 * [androidx.compose.ui.viewinterop.AndroidView].
 */
class DiceGLView(context: Context, renderer: DiceRenderer) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        // RENDERMODE_CONTINUOUSLY drives physics at the display refresh rate (~60 Hz)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
