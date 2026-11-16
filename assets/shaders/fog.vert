// Attributs fournis automatiquement par SpriteBatch pour chaque sommet.
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

// Matrice fournie par SpriteBatch à partir de camera.combined.
uniform mat4 u_projTrans;

// Valeurs transmises au fragment shader et interpolées entre les sommets.
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_worldPosition;

void main() {
    v_color = a_color;
    // Correction standard de SpriteBatch pour sa couleur encodée.
    v_color.a *= 255.0 / 254.0;
    v_texCoords = a_texCoord0;

    // Avec notre SpriteBatch, les positions de draw sont en unités du monde.
    // On les conserve AVANT la projection pour placer le cercle et le snap.
    v_worldPosition = a_position.xy;
    gl_Position = u_projTrans * a_position;
}
