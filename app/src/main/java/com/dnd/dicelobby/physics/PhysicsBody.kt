package com.dnd.dicelobby.physics

import kotlin.math.abs

/**
 * Rigid-body state for a single die.
 *
 * @param position        World-space centre of the die.
 * @param velocity        Linear velocity (m/s).
 * @param orientation     Unit quaternion representing the die's current rotation.
 * @param angularVelocity Angular velocity vector (rad/s), magnitude = rotation speed.
 * @param mass            Die mass in kg (default 0.04 kg ≈ a plastic d20).
 * @param restitution     Coefficient of restitution for bouncing (0 = no bounce, 1 = elastic).
 * @param friction        Surface friction coefficient.
 * @param halfSize        Half-extent of the bounding cube used for ground collision.
 * @param settled         True once the die has come to rest — physics integration stops.
 * @param faceIndex       Index into the die's face-normal array of the top-most face.
 *                        Set when [settled] becomes true.
 */
data class PhysicsBody(
    var position: Vector3        = Vector3(0f, 1f, 0f),
    var velocity: Vector3        = Vector3(),
    var orientation: Quaternion  = Quaternion.identity(),
    var angularVelocity: Vector3 = Vector3(),
    val mass: Float              = 0.04f,
    val restitution: Float       = 0.35f,
    val friction: Float          = 0.65f,
    val halfSize: Float          = 0.25f,
    var settled: Boolean         = false,
    var faceIndex: Int           = 0
) {
    /** True if the body is nearly at rest (low linear + angular speed). */
    fun isAtRest(): Boolean =
        velocity.lengthSq() < 0.01f && angularVelocity.lengthSq() < 0.05f

    /** Dampen angular velocity to simulate rolling friction. */
    fun applyAngularDamping(dt: Float, damping: Float = 1.8f) {
        angularVelocity = angularVelocity * (1f - damping * dt).coerceAtLeast(0f)
    }

    /** Dampen linear velocity to simulate surface friction. */
    fun applyLinearDamping(dt: Float, damping: Float = 1.2f) {
        velocity = velocity * (1f - damping * dt).coerceAtLeast(0f)
    }
}
