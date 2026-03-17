package com.dnd.dicelobby.physics

import com.dnd.dicelobby.dice.DiceType
import kotlin.math.*
import kotlin.random.Random

/**
 * Simulates a table-top physics scene containing multiple dice.
 *
 * The world uses a simple semi-implicit Euler integrator with:
 *   • Gravity along −Y
 *   • Flat ground plane at Y = 0
 *   • Four vertical walls forming a 4 m × 4 m arena
 *   • Sphere-plane collision (each die treated as a sphere of radius [PhysicsBody.halfSize])
 *   • Angular damping + linear damping once contact is made
 *
 * Face detection:
 *   After a body settles, we rotate each face normal by the body's orientation quaternion
 *   and select the face whose world-space normal is most aligned with +Y (up). That face's
 *   integer value is used as the die result — ensuring the simulation drives the outcome,
 *   not a separate RNG call.
 */
class PhysicsWorld {

    companion object {
        private const val GRAVITY     = -9.81f
        private const val ARENA_HALF  = 2.0f        // half-width of the arena
        private const val GROUND_Y    = 0.0f
        private const val SETTLE_TIME = 0.6f         // seconds body must stay at rest before marking settled
    }

    val bodies = mutableListOf<PhysicsBody>()

    /** Accumulated time (per-body) that the body has been at rest. */
    private val restTimers = mutableMapOf<PhysicsBody, Float>()

    /** Whether all bodies have settled. */
    val allSettled: Boolean get() = bodies.isNotEmpty() && bodies.all { it.settled }

    // Face normals in local (body) space for each die type.
    // These are the outward normals of each numbered face, stored in face-value order
    // (index 0 = face value 1, index 1 = face value 2, etc.).
    private val faceNormals: Map<DiceType, List<Vector3>> = buildFaceNormals()

    /**
     * Advance the simulation by [dt] seconds.
     * Call this from your rendering loop at ~60 Hz.
     */
    fun step(dt: Float) {
        bodies.forEach { body ->
            if (body.settled) return@forEach

            // --- Linear integration ---
            body.velocity = body.velocity + Vector3(0f, GRAVITY * dt, 0f)
            body.position = body.position + body.velocity * dt

            // --- Angular integration ---
            // Convert angular velocity to a rotation delta and multiply into orientation
            val angle = body.angularVelocity.length()
            if (angle > 1e-6f) {
                val axis = body.angularVelocity.normalized()
                val dq   = Quaternion.fromAxisAngle(axis, angle * dt)
                body.orientation = (body.orientation * dq).normalized()
            }

            // --- Ground collision ---
            val groundContact = body.position.y - body.halfSize < GROUND_Y
            if (groundContact) {
                body.position.y = GROUND_Y + body.halfSize
                if (body.velocity.y < 0f) {
                    body.velocity.y = -body.velocity.y * body.restitution
                }
                // Sliding friction on the XZ plane
                body.applyLinearDamping(dt, body.friction * 2f)
                body.applyAngularDamping(dt, 4.0f)
            }

            // --- Wall collisions ---
            resolveWall(body, dt)

            // --- Body damping in air (air resistance) ---
            if (!groundContact) {
                body.applyAngularDamping(dt, 0.2f)
            }

            // --- Settle check ---
            if (body.isAtRest() && body.position.y - body.halfSize <= GROUND_Y + 0.01f) {
                val timer = (restTimers[body] ?: 0f) + dt
                restTimers[body] = timer
                if (timer >= SETTLE_TIME) {
                    body.settled  = true
                    body.faceIndex = detectTopFace(body)
                }
            } else {
                restTimers[body] = 0f
            }
        }
    }

    /** Resolve axis-aligned box vs wall collisions on X and Z axes. */
    private fun resolveWall(body: PhysicsBody, dt: Float) {
        if (body.position.x + body.halfSize > ARENA_HALF) {
            body.position.x = ARENA_HALF - body.halfSize
            body.velocity.x = -abs(body.velocity.x) * body.restitution
        }
        if (body.position.x - body.halfSize < -ARENA_HALF) {
            body.position.x = -ARENA_HALF + body.halfSize
            body.velocity.x = abs(body.velocity.x) * body.restitution
        }
        if (body.position.z + body.halfSize > ARENA_HALF) {
            body.position.z = ARENA_HALF - body.halfSize
            body.velocity.z = -abs(body.velocity.z) * body.restitution
        }
        if (body.position.z - body.halfSize < -ARENA_HALF) {
            body.position.z = -ARENA_HALF + body.halfSize
            body.velocity.z = abs(body.velocity.z) * body.restitution
        }
    }

    /**
     * Detect which face is on top by finding the local face normal most aligned with
     * the world UP direction after applying the body's orientation.
     *
     * Returns a 0-based face index (value = faceIndex + 1 for standard dice).
     */
    private fun detectTopFace(body: PhysicsBody): Int {
        // We need to transform world UP into local space (inverse of orientation = conjugate)
        val localUp = body.orientation.rotateVector(Vector3.UP)

        // TODO: use actual die type; for the generic case fall back to 6-face normals
        val normals = faceNormals[DiceType.D6] ?: return 0

        var best = -1
        var bestDot = Float.NEGATIVE_INFINITY
        normals.forEachIndexed { i, n ->
            val d = n.dot(localUp)
            if (d > bestDot) { bestDot = d; best = i }
        }
        return best
    }

    /**
     * External version of face detection that accepts a [DiceType], used after simulation.
     * Returns the 1-based die value corresponding to the top face.
     */
    fun detectTopFaceValue(body: PhysicsBody, diceType: DiceType): Int {
        val localUp = body.orientation.rotateVector(Vector3.UP)
        val normals = faceNormals[diceType] ?: faceNormals[DiceType.D6]!!
        var best    = 0
        var bestDot = Float.NEGATIVE_INFINITY
        normals.forEachIndexed { i, n ->
            val d = n.dot(localUp)
            if (d > bestDot) { bestDot = d; best = i }
        }
        // face index is 0-based; face value for standard dice = index + 1
        return (best + 1).coerceIn(1, diceType.faces)
    }

    /** Spawn a die at a randomised position above the table with a random impulse. */
    fun spawnDie(random: Random = Random.Default): PhysicsBody {
        val body = PhysicsBody(
            position = Vector3(
                x = random.nextFloat() * 2f - 1f,
                y = random.nextFloat() * 0.5f + 1.2f,
                z = random.nextFloat() * 2f - 1f
            ),
            velocity = Vector3(
                x = (random.nextFloat() - 0.5f) * 3f,
                y = random.nextFloat() * 1.5f + 0.5f,
                z = (random.nextFloat() - 0.5f) * 3f
            ),
            angularVelocity = Vector3(
                x = (random.nextFloat() - 0.5f) * 10f,
                y = (random.nextFloat() - 0.5f) * 10f,
                z = (random.nextFloat() - 0.5f) * 10f
            ),
            orientation = Quaternion.fromAxisAngle(
                Vector3(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalized(),
                random.nextFloat() * 2f * PI.toFloat()
            )
        )
        bodies.add(body)
        return body
    }

    fun clear() {
        bodies.clear()
        restTimers.clear()
    }

    // -------------------------------------------------------------------------
    // Face normal tables
    // -------------------------------------------------------------------------

    /**
     * Builds outward face normals in local space for each die type.
     * Index 0 = face showing "1", index 1 = face showing "2", etc.
     */
    private fun buildFaceNormals(): Map<DiceType, List<Vector3>> {
        val map = mutableMapOf<DiceType, List<Vector3>>()

        // d6: axis-aligned faces
        // Standard Western d6: opposite faces sum to 7
        //   face 1 = −Y, face 6 = +Y  (1 opposite 6)
        //   face 2 = −Z, face 5 = +Z
        //   face 3 = −X, face 4 = +X
        map[DiceType.D6] = listOf(
            Vector3(0f, -1f, 0f),  // 1 (bottom)
            Vector3(0f,  0f, -1f), // 2 (back)
            Vector3(-1f, 0f,  0f), // 3 (left)
            Vector3( 1f, 0f,  0f), // 4 (right)
            Vector3(0f,  0f,  1f), // 5 (front)
            Vector3(0f,  1f,  0f)  // 6 (top)
        )

        // d4: tetrahedron — 4 equilateral-triangle faces
        val sqrt3 = sqrt(3f)
        map[DiceType.D4] = listOf(
            Vector3( 0f,          -1f,         2f / sqrt3).normalized(),
            Vector3( sqrt3 / 2f,  -1f,        -1f / sqrt3).normalized(),
            Vector3(-sqrt3 / 2f,  -1f,        -1f / sqrt3).normalized(),
            Vector3( 0f,           1f,          0f).normalized()
        )

        // d8: octahedron — 8 triangular faces
        val inv3 = 1f / sqrt(3f)
        map[DiceType.D8] = listOf(
            Vector3( inv3,  inv3,  inv3),
            Vector3(-inv3,  inv3,  inv3),
            Vector3( inv3, -inv3,  inv3),
            Vector3(-inv3, -inv3,  inv3),
            Vector3( inv3,  inv3, -inv3),
            Vector3(-inv3,  inv3, -inv3),
            Vector3( inv3, -inv3, -inv3),
            Vector3(-inv3, -inv3, -inv3)
        )

        // d10: pentagonal trapezohedron — approximate with 10 normals
        map[DiceType.D10] = (0 until 10).map { i ->
            val angle = (2f * PI * i / 10f).toFloat()
            val tilt  = if (i % 2 == 0) 0.4f else -0.4f
            Vector3(cos(angle), tilt, sin(angle)).normalized()
        }

        // d12: dodecahedron — 12 pentagonal faces
        val phi = (1f + sqrt(5f)) / 2f
        map[DiceType.D12] = listOf(
            Vector3( 0f,  1f,  phi), Vector3( 0f,  1f, -phi),
            Vector3( 0f, -1f,  phi), Vector3( 0f, -1f, -phi),
            Vector3( phi,  0f,  1f), Vector3( phi,  0f, -1f),
            Vector3(-phi,  0f,  1f), Vector3(-phi,  0f, -1f),
            Vector3( 1f,  phi,  0f), Vector3( 1f, -phi,  0f),
            Vector3(-1f,  phi,  0f), Vector3(-1f, -phi,  0f)
        ).map { it.normalized() }

        // d20: icosahedron — 20 triangular faces
        val t = (1f + sqrt(5f)) / 2f
        val verts = listOf(
            Vector3(-1f,  t,  0f), Vector3( 1f,  t,  0f),
            Vector3(-1f, -t,  0f), Vector3( 1f, -t,  0f),
            Vector3( 0f, -1f,  t), Vector3( 0f,  1f,  t),
            Vector3( 0f, -1f, -t), Vector3( 0f,  1f, -t),
            Vector3( t,  0f, -1f), Vector3( t,  0f,  1f),
            Vector3(-t,  0f, -1f), Vector3(-t,  0f,  1f)
        )
        val faces20 = listOf(
            Triple(0,11,5), Triple(0,5,1), Triple(0,1,7), Triple(0,7,10), Triple(0,10,11),
            Triple(1,5,9),  Triple(5,11,4),Triple(11,10,2),Triple(10,7,6), Triple(7,1,8),
            Triple(3,9,4),  Triple(3,4,2), Triple(3,2,6), Triple(3,6,8),  Triple(3,8,9),
            Triple(4,9,5),  Triple(2,4,11),Triple(6,2,10),Triple(8,6,7),  Triple(9,8,1)
        )
        map[DiceType.D20] = faces20.map { (a, b, c) ->
            ((verts[a] + verts[b] + verts[c]) * (1f / 3f)).normalized()
        }

        // d100: same shape as d10 but 10 normals (tens digit)
        map[DiceType.D100] = map[DiceType.D10]!!

        return map
    }
}
