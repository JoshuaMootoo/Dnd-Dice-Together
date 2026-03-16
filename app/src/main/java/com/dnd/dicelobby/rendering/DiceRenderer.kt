package com.dnd.dicelobby.rendering

import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.dnd.dicelobby.dice.DiceType
import com.dnd.dicelobby.physics.PhysicsBody
import com.dnd.dicelobby.physics.PhysicsWorld
import com.dnd.dicelobby.physics.Quaternion
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

/**
 * OpenGL ES 2.0 renderer for the dice animation overlay.
 *
 * Coordinate system:
 *   • World Y-axis is up.
 *   • Camera is positioned directly above looking straight down (+Y → −Z in view).
 *   • Light comes from the upper-left (slightly from behind the camera).
 *
 * Rendering loop:
 *   1. Advance [PhysicsWorld] by the elapsed frame time.
 *   2. For each die in the world, build a model matrix from position + orientation.
 *   3. Draw the die mesh using Blinn-Phong shading tinted to the player's colour.
 *   4. Once [PhysicsWorld.allSettled] is true, notify [onSettled] with per-die results.
 */
class DiceRenderer(
    private val physicsWorld: PhysicsWorld,
    private val diceType: DiceType,
    private val playerColor: Int,            // Android Color int
    private val onSettled: (List<Int>) -> Unit  // called once, on GL thread
) : GLSurfaceView.Renderer {

    // OpenGL handle IDs
    private var program    = 0
    private var aPosition  = 0
    private var aNormal    = 0
    private var uMVP       = 0
    private var uModel     = 0
    private var uColor     = 0
    private var uLightDir  = 0
    private var uCamPos    = 0

    private lateinit var mesh: DiceMeshFactory.Mesh

    // Matrices
    private val projMatrix  = FloatArray(16)
    private val viewMatrix  = FloatArray(16)
    private val mvpMatrix   = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val tempMatrix  = FloatArray(16)

    // Timing
    private var lastFrameTime = System.nanoTime()
    private var settledNotified = false

    // Player colour as normalised RGB
    private val colorR = Color.red(playerColor)   / 255f
    private val colorG = Color.green(playerColor) / 255f
    private val colorB = Color.blue(playerColor)  / 255f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.10f, 0.08f, 0.15f, 1f)  // dark purple table
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)

        program = createProgram(DiceShaders.VERTEX_SHADER, DiceShaders.FRAGMENT_SHADER)
        aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        aNormal   = GLES20.glGetAttribLocation(program, "aNormal")
        uMVP      = GLES20.glGetUniformLocation(program, "uMVP")
        uModel    = GLES20.glGetUniformLocation(program, "uModel")
        uColor    = GLES20.glGetUniformLocation(program, "uColor")
        uLightDir = GLES20.glGetUniformLocation(program, "uLightDir")
        uCamPos   = GLES20.glGetUniformLocation(program, "uCamPos")

        mesh = DiceMeshFactory.create(diceType)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height
        // Top-down orthographic projection — arena is 4 m wide
        Matrix.orthoM(projMatrix, 0, -2f * aspect, 2f * aspect, -2f, 2f, 0.1f, 20f)
        // Camera directly above, looking down
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 8f, 0f,   // eye
            0f, 0f, 0f,   // center
            0f, 0f, -1f   // up vector in screen space
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        // --- Physics step ---
        val now   = System.nanoTime()
        val dt    = ((now - lastFrameTime) / 1_000_000_000f).coerceIn(0f, 0.05f)
        lastFrameTime = now
        physicsWorld.step(dt)

        // --- Render ---
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        // View-projection combined
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

        // Light direction (from upper-left, slightly in front of camera)
        GLES20.glUniform3f(uLightDir, 0.4f, 1f, 0.3f)
        GLES20.glUniform3f(uCamPos,   0f,   8f, 0f)
        GLES20.glUniform3f(uColor, colorR, colorG, colorB)

        // Draw each die
        physicsWorld.bodies.forEach { body -> drawBody(body) }

        // --- Settle callback ---
        if (!settledNotified && physicsWorld.allSettled) {
            settledNotified = true
            val results = physicsWorld.bodies.map { body ->
                physicsWorld.detectTopFaceValue(body, diceType)
            }
            onSettled(results)
        }
    }

    private fun drawBody(body: PhysicsBody) {
        // Build model matrix: rotation from quaternion, then translate
        val rot = body.orientation.toMatrix()
        // Column-major model = T * R (translate then rotate in world space)
        Matrix.setIdentityM(modelMatrix, 0)
        System.arraycopy(rot, 0, modelMatrix, 0, 16)
        modelMatrix[12] = body.position.x
        modelMatrix[13] = body.position.y
        modelMatrix[14] = body.position.z

        Matrix.multiplyMM(tempMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVP,   1, false, tempMatrix,  0)
        GLES20.glUniformMatrix4fv(uModel, 1, false, modelMatrix, 0)

        // Bind vertex buffer (interleaved position + normal, 6 floats × 4 bytes = 24 bytes)
        val stride = 6 * 4
        mesh.vertices.position(0)
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, stride, mesh.vertices)
        GLES20.glEnableVertexAttribArray(aPosition)

        mesh.vertices.position(3)
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, stride, mesh.vertices)
        GLES20.glEnableVertexAttribArray(aNormal)

        mesh.indices.position(0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.indexCount, GLES20.GL_UNSIGNED_SHORT, mesh.indices)
    }

    // ── Shader compilation helpers ────────────────────────────────────────────

    private fun createProgram(vertSrc: String, fragSrc: String): Int {
        val vert = compileShader(GLES20.GL_VERTEX_SHADER,   vertSrc)
        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
        return GLES20.glCreateProgram().also { prog ->
            GLES20.glAttachShader(prog, vert)
            GLES20.glAttachShader(prog, frag)
            GLES20.glLinkProgram(prog)
        }
    }

    private fun compileShader(type: Int, src: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, src)
        GLES20.glCompileShader(shader)
        return shader
    }
}
