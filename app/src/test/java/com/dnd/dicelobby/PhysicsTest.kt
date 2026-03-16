package com.dnd.dicelobby

import com.dnd.dicelobby.physics.PhysicsWorld
import com.dnd.dicelobby.physics.Vector3
import com.dnd.dicelobby.physics.Quaternion
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for the physics engine.
 */
class PhysicsTest {

    @Test
    fun `spawned die eventually settles`() {
        val world  = PhysicsWorld()
        val random = Random(42)
        repeat(3) { world.spawnDie(random) }

        // Simulate up to 5 seconds at 60 Hz
        var steps = 0
        while (!world.allSettled && steps < 300) {
            world.step(1f / 60f)
            steps++
        }
        assertTrue("Dice should settle within 5 seconds", world.allSettled)
    }

    @Test
    fun `vector cross product is correct`() {
        val a   = Vector3(1f, 0f, 0f)
        val b   = Vector3(0f, 1f, 0f)
        val axb = a.cross(b)
        assertEquals(0f, axb.x, 1e-5f)
        assertEquals(0f, axb.y, 1e-5f)
        assertEquals(1f, axb.z, 1e-5f)
    }

    @Test
    fun `quaternion identity rotates vector unchanged`() {
        val q = Quaternion.identity()
        val v = Vector3(1f, 2f, 3f)
        val r = q.rotateVector(v)
        assertEquals(v.x, r.x, 1e-5f)
        assertEquals(v.y, r.y, 1e-5f)
        assertEquals(v.z, r.z, 1e-5f)
    }

    @Test
    fun `quaternion 180 deg rotation around Y inverts X`() {
        val q = Quaternion.fromAxisAngle(Vector3(0f, 1f, 0f), Math.PI.toFloat())
        val v = Vector3(1f, 0f, 0f)
        val r = q.rotateVector(v)
        assertEquals(-1f, r.x, 1e-4f)
        assertEquals( 0f, r.y, 1e-4f)
        assertEquals( 0f, r.z, 1e-4f)
    }
}
