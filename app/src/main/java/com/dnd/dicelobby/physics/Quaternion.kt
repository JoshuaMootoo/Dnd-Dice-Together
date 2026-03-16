package com.dnd.dicelobby.physics

import kotlin.math.*

/**
 * Unit quaternion representing a 3-D rotation.
 * Used to track die orientation without gimbal-lock issues.
 */
data class Quaternion(
    var w: Float = 1f,
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    /** Hamilton product: rotate `this` then `other`. */
    operator fun times(other: Quaternion) = Quaternion(
        w = w * other.w - x * other.x - y * other.y - z * other.z,
        x = w * other.x + x * other.w + y * other.z - z * other.y,
        y = w * other.y - x * other.z + y * other.w + z * other.x,
        z = w * other.z + x * other.y - y * other.x + z * other.w
    ).normalized()

    fun normalized(): Quaternion {
        val n = sqrt(w * w + x * x + y * y + z * z)
        return if (n < 1e-6f) identity() else Quaternion(w / n, x / n, y / n, z / n)
    }

    /**
     * Rotate a world-space vector into local (body) space.
     * Equivalent to: q⁻¹ * Vector3Quaternion * q
     */
    fun rotateVector(v: Vector3): Vector3 {
        // Optimised formula (no need to build pure quaternion product)
        val qv = Vector3(x, y, z)
        val uv = qv.cross(v)
        val uuv = qv.cross(uv)
        return v + (uv * (2f * w)) + (uuv * 2f)
    }

    /**
     * Convert to a 4×4 column-major rotation matrix suitable for OpenGL.
     * The returned array contains 16 floats.
     */
    fun toMatrix(): FloatArray {
        val xx = x * x; val yy = y * y; val zz = z * z
        val xy = x * y; val xz = x * z; val yz = y * z
        val wx = w * x; val wy = w * y; val wz = w * z
        return floatArrayOf(
            1 - 2*(yy+zz), 2*(xy+wz),     2*(xz-wy),     0f,
            2*(xy-wz),     1 - 2*(xx+zz), 2*(yz+wx),     0f,
            2*(xz+wy),     2*(yz-wx),     1 - 2*(xx+yy), 0f,
            0f,            0f,            0f,            1f
        )
    }

    companion object {
        fun identity() = Quaternion(1f, 0f, 0f, 0f)

        /** Create a quaternion from an axis + angle (radians). */
        fun fromAxisAngle(axis: Vector3, angleRad: Float): Quaternion {
            val half = angleRad / 2f
            val s = sin(half)
            val n = axis.normalized()
            return Quaternion(cos(half), n.x * s, n.y * s, n.z * s)
        }
    }
}
