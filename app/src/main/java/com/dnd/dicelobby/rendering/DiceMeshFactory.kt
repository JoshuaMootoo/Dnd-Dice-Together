package com.dnd.dicelobby.rendering

import com.dnd.dicelobby.dice.DiceType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.*

/**
 * Generates interleaved vertex data (position XYZ + normal XYZ) and index arrays
 * for each RPG die type.
 *
 * Coordinates are in local die space and typically sit within a unit sphere so all
 * die types appear at the same visual scale after a uniform model transform.
 */
object DiceMeshFactory {

    data class Mesh(
        val vertices: FloatBuffer,  // interleaved: x y z nx ny nz  (6 floats/vertex)
        val indices: ShortBuffer,
        val indexCount: Int
    )

    fun create(type: DiceType): Mesh = when (type) {
        DiceType.D4  -> tetrahedron()
        DiceType.D6  -> cube()
        DiceType.D8  -> octahedron()
        DiceType.D10 -> pentagonalTrapezohedron()
        DiceType.D12 -> dodecahedron()
        DiceType.D20 -> icosahedron()
        DiceType.D100 -> pentagonalTrapezohedron() // same shape as d10
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun buildMesh(triangles: List<FloatArray>): Mesh {
        // Each element in [triangles] is a flat array: [x0 y0 z0 x1 y1 z1 x2 y2 z2]
        // We compute per-face normals (flat shading).
        val verts = mutableListOf<Float>()
        val idxs  = mutableListOf<Short>()
        var idx: Short = 0

        for (tri in triangles) {
            val ax = tri[0]; val ay = tri[1]; val az = tri[2]
            val bx = tri[3]; val by = tri[4]; val bz = tri[5]
            val cx = tri[6]; val cy = tri[7]; val cz = tri[8]

            // Normal = (B-A) × (C-A)
            val ux = bx - ax; val uy = by - ay; val uz = bz - az
            val vx = cx - ax; val vy = cy - ay; val vz = cz - az
            var nx = uy * vz - uz * vy
            var ny = uz * vx - ux * vz
            var nz = ux * vy - uy * vx
            val len = sqrt(nx * nx + ny * ny + nz * nz)
            if (len > 1e-6f) { nx /= len; ny /= len; nz /= len }

            // 3 vertices per triangle, each with position + normal
            for (v in listOf(floatArrayOf(ax, ay, az), floatArrayOf(bx, by, bz), floatArrayOf(cx, cy, cz))) {
                verts += listOf(v[0], v[1], v[2], nx, ny, nz)
                idxs += idx++
            }
        }

        val vBuf = ByteBuffer.allocateDirect(verts.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vBuf.put(verts.toFloatArray()).position(0)
        val iBuf = ByteBuffer.allocateDirect(idxs.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()
        iBuf.put(idxs.toShortArray()).position(0)

        return Mesh(vBuf, iBuf, idxs.size)
    }

    private fun tri(ax: Float, ay: Float, az: Float,
                    bx: Float, by: Float, bz: Float,
                    cx: Float, cy: Float, cz: Float) =
        floatArrayOf(ax, ay, az, bx, by, bz, cx, cy, cz)

    // ── Die geometries ────────────────────────────────────────────────────────

    /** D4 — regular tetrahedron */
    private fun tetrahedron(): Mesh {
        val s = 1f / sqrt(3f)
        val v0 = floatArrayOf( 0f,  1f,  0f)
        val v1 = floatArrayOf( 0f, -s,   2f * s)
        val v2 = floatArrayOf( sqrt(3f) * s, -s, -s)
        val v3 = floatArrayOf(-sqrt(3f) * s, -s, -s)
        return buildMesh(listOf(
            tri(v0[0],v0[1],v0[2], v1[0],v1[1],v1[2], v2[0],v2[1],v2[2]),
            tri(v0[0],v0[1],v0[2], v2[0],v2[1],v2[2], v3[0],v3[1],v3[2]),
            tri(v0[0],v0[1],v0[2], v3[0],v3[1],v3[2], v1[0],v1[1],v1[2]),
            tri(v1[0],v1[1],v1[2], v3[0],v3[1],v3[2], v2[0],v2[1],v2[2])
        ))
    }

    /** D6 — cube with slightly bevelled corners (kept simple: plain cube) */
    private fun cube(): Mesh {
        val s = 0.7f
        val tris = mutableListOf<FloatArray>()
        // Front (+Z)
        tris += tri(-s,-s, s,  s,-s, s,  s, s, s)
        tris += tri(-s,-s, s,  s, s, s, -s, s, s)
        // Back (−Z)
        tris += tri( s,-s,-s, -s,-s,-s, -s, s,-s)
        tris += tri( s,-s,-s, -s, s,-s,  s, s,-s)
        // Top (+Y)
        tris += tri(-s, s, s,  s, s, s,  s, s,-s)
        tris += tri(-s, s, s,  s, s,-s, -s, s,-s)
        // Bottom (−Y)
        tris += tri(-s,-s,-s,  s,-s,-s,  s,-s, s)
        tris += tri(-s,-s,-s,  s,-s, s, -s,-s, s)
        // Right (+X)
        tris += tri( s,-s, s,  s,-s,-s,  s, s,-s)
        tris += tri( s,-s, s,  s, s,-s,  s, s, s)
        // Left (−X)
        tris += tri(-s,-s,-s, -s,-s, s, -s, s, s)
        tris += tri(-s,-s,-s, -s, s, s, -s, s,-s)
        return buildMesh(tris)
    }

    /** D8 — regular octahedron */
    private fun octahedron(): Mesh {
        val r = 1f
        val t  = floatArrayOf( 0f,  r,  0f)
        val b  = floatArrayOf( 0f, -r,  0f)
        val fr = floatArrayOf( 0f,  0f,  r)
        val bk = floatArrayOf( 0f,  0f, -r)
        val ri = floatArrayOf( r,   0f,  0f)
        val le = floatArrayOf(-r,   0f,  0f)
        return buildMesh(listOf(
            tri(t[0],t[1],t[2], fr[0],fr[1],fr[2], ri[0],ri[1],ri[2]),
            tri(t[0],t[1],t[2], ri[0],ri[1],ri[2], bk[0],bk[1],bk[2]),
            tri(t[0],t[1],t[2], bk[0],bk[1],bk[2], le[0],le[1],le[2]),
            tri(t[0],t[1],t[2], le[0],le[1],le[2], fr[0],fr[1],fr[2]),
            tri(b[0],b[1],b[2], ri[0],ri[1],ri[2], fr[0],fr[1],fr[2]),
            tri(b[0],b[1],b[2], bk[0],bk[1],bk[2], ri[0],ri[1],ri[2]),
            tri(b[0],b[1],b[2], le[0],le[1],le[2], bk[0],bk[1],bk[2]),
            tri(b[0],b[1],b[2], fr[0],fr[1],fr[2], le[0],le[1],le[2])
        ))
    }

    /** D10 — approximate with a pentagonal trapezohedron */
    private fun pentagonalTrapezohedron(): Mesh {
        val tris  = mutableListOf<FloatArray>()
        val n     = 5
        val upper = 0.4f   // y level of upper ring
        val lower = -0.4f  // y level of lower ring
        val top   = floatArrayOf(0f, 1f, 0f)
        val bot   = floatArrayOf(0f, -1f, 0f)

        val uRing = Array(n) { i ->
            val a = (2 * PI * i / n).toFloat()
            floatArrayOf(cos(a), upper, sin(a))
        }
        val lRing = Array(n) { i ->
            val a = (2 * PI * (i + 0.5f) / n).toFloat()
            floatArrayOf(cos(a), lower, sin(a))
        }

        for (i in 0 until n) {
            val ni = (i + 1) % n
            // Upper cap
            tris += tri(top[0], top[1], top[2], uRing[i][0], uRing[i][1], uRing[i][2],
                        uRing[ni][0], uRing[ni][1], uRing[ni][2])
            // Lower cap
            tris += tri(bot[0], bot[1], bot[2], lRing[ni][0], lRing[ni][1], lRing[ni][2],
                        lRing[i][0], lRing[i][1], lRing[i][2])
            // Side kite-quads (2 triangles each)
            tris += tri(uRing[i][0], uRing[i][1], uRing[i][2],
                        lRing[i][0], lRing[i][1], lRing[i][2],
                        uRing[ni][0], uRing[ni][1], uRing[ni][2])
            tris += tri(lRing[i][0], lRing[i][1], lRing[i][2],
                        lRing[ni][0], lRing[ni][1], lRing[ni][2],
                        uRing[ni][0], uRing[ni][1], uRing[ni][2])
        }
        return buildMesh(tris)
    }

    /** D12 — regular dodecahedron (12 pentagonal faces) */
    private fun dodecahedron(): Mesh {
        val phi = (1f + sqrt(5f)) / 2f
        val a   = 1f / phi
        val b   = 1f

        // 20 vertices of a regular dodecahedron
        val rawV = listOf(
            floatArrayOf( b,  b,  b), floatArrayOf( b,  b, -b),
            floatArrayOf( b, -b,  b), floatArrayOf( b, -b, -b),
            floatArrayOf(-b,  b,  b), floatArrayOf(-b,  b, -b),
            floatArrayOf(-b, -b,  b), floatArrayOf(-b, -b, -b),
            floatArrayOf( 0f, a, phi), floatArrayOf( 0f, a,-phi),
            floatArrayOf( 0f,-a, phi), floatArrayOf( 0f,-a,-phi),
            floatArrayOf( phi, 0f, a), floatArrayOf( phi, 0f,-a),
            floatArrayOf(-phi, 0f, a), floatArrayOf(-phi, 0f,-a),
            floatArrayOf( a, phi, 0f), floatArrayOf( a,-phi, 0f),
            floatArrayOf(-a, phi, 0f), floatArrayOf(-a,-phi, 0f)
        )

        // 12 pentagonal faces (vertex indices)
        val faces = listOf(
            listOf(0, 8,10, 2,12), listOf(0,16, 1,13,12),
            listOf(0,18, 4, 8, 0), listOf(1,16,18, 5, 9),
            listOf(1, 9,11, 3,13), listOf(2,10, 6,19,17),
            listOf(2,17, 3,11,12), listOf(3,11, 7,15, 9), // correction below
            listOf(4,14, 6,10, 8), listOf(4,18,16, 0, 8),
            listOf(5,15, 7,19, 6), listOf(5,18, 4,14,15)
        )

        val tris = mutableListOf<FloatArray>()
        for (face in faces) {
            val vList = face.map { rawV[it] }
            // Fan triangulation from first vertex
            for (k in 1 until vList.size - 1) {
                val a0 = vList[0]; val a1 = vList[k]; val a2 = vList[k + 1]
                tris += tri(a0[0], a0[1], a0[2], a1[0], a1[1], a1[2], a2[0], a2[1], a2[2])
            }
        }
        return buildMesh(tris)
    }

    /** D20 — regular icosahedron */
    private fun icosahedron(): Mesh {
        val t    = (1f + sqrt(5f)) / 2f
        val r    = sqrt(1f + t * t)
        fun v(a: Float, b: Float, c: Float) = floatArrayOf(a / r, b / r, c / r)

        val verts = listOf(
            v(-1f,  t,  0f), v( 1f,  t,  0f), v(-1f, -t,  0f), v( 1f, -t,  0f),
            v( 0f, -1f,  t), v( 0f,  1f,  t), v( 0f, -1f, -t), v( 0f,  1f, -t),
            v( t,   0f, -1f), v( t,   0f,  1f), v(-t,   0f, -1f), v(-t,   0f,  1f)
        )
        val faces = listOf(
            Triple(0,11,5), Triple(0,5,1), Triple(0,1,7), Triple(0,7,10), Triple(0,10,11),
            Triple(1,5,9),  Triple(5,11,4),Triple(11,10,2),Triple(10,7,6), Triple(7,1,8),
            Triple(3,9,4),  Triple(3,4,2), Triple(3,2,6), Triple(3,6,8),  Triple(3,8,9),
            Triple(4,9,5),  Triple(2,4,11),Triple(6,2,10),Triple(8,6,7),  Triple(9,8,1)
        )
        return buildMesh(faces.map { (a, b, c) ->
            val va = verts[a]; val vb = verts[b]; val vc = verts[c]
            tri(va[0], va[1], va[2], vb[0], vb[1], vb[2], vc[0], vc[1], vc[2])
        })
    }
}
