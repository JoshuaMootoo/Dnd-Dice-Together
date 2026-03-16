package com.dnd.dicelobby.rendering

/**
 * GLSL source code for the dice renderer.
 *
 * Design:
 *   Vertex shader  — transforms vertices with MVP matrix, passes normal + position
 *                    to fragment shader.
 *   Fragment shader — Blinn-Phong lighting from a single overhead directional light.
 *                    Face colour is a uniform so each face can be coloured individually
 *                    (or the whole die tinted to the player's colour).
 */
object DiceShaders {

    val VERTEX_SHADER = """
        uniform   mat4 uMVP;
        uniform   mat4 uModel;
        attribute vec4 aPosition;
        attribute vec3 aNormal;
        varying   vec3 vNormalWorld;
        varying   vec3 vPosWorld;

        void main() {
            vec4 worldPos  = uModel * aPosition;
            vPosWorld      = worldPos.xyz;
            vNormalWorld   = normalize(mat3(uModel) * aNormal);
            gl_Position    = uMVP * aPosition;
        }
    """.trimIndent()

    val FRAGMENT_SHADER = """
        precision mediump float;
        uniform vec3 uColor;
        uniform vec3 uLightDir;   // normalised direction TO the light
        uniform vec3 uCamPos;
        varying vec3 vNormalWorld;
        varying vec3 vPosWorld;

        void main() {
            vec3 N    = normalize(vNormalWorld);
            vec3 L    = normalize(uLightDir);
            vec3 V    = normalize(uCamPos - vPosWorld);
            vec3 H    = normalize(L + V);

            float diff   = max(dot(N, L), 0.0);
            float spec   = pow(max(dot(N, H), 0.0), 32.0);
            float ambient = 0.25;

            vec3 finalColor = uColor * (ambient + 0.7 * diff) + vec3(0.3) * spec;
            gl_FragColor    = vec4(clamp(finalColor, 0.0, 1.0), 1.0);
        }
    """.trimIndent()
}
