package com.dnd.dicelobby.physics

import kotlin.math.sqrt

/** Lightweight mutable 3-D vector used throughout the physics engine. */
data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {

    operator fun plus(other: Vector3)  = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float)  = Vector3(x * scalar,  y * scalar,  z * scalar)
    operator fun unaryMinus()          = Vector3(-x, -y, -z)

    fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z

    fun cross(other: Vector3) = Vector3(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    fun length() = sqrt(x * x + y * y + z * z)
    fun lengthSq() = x * x + y * y + z * z

    fun normalized(): Vector3 {
        val len = length()
        return if (len < 1e-6f) Vector3() else Vector3(x / len, y / len, z / len)
    }

    fun set(other: Vector3) { x = other.x; y = other.y; z = other.z }

    companion object {
        val UP    = Vector3(0f, 1f, 0f)
        val ZERO  = Vector3(0f, 0f, 0f)
    }
}
