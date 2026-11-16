#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_worldPosition;

// SpriteBatch associe automatiquement la texture dessinée à u_texture.
uniform sampler2D u_texture;
uniform sampler2D u_snapMask;
uniform vec2 u_snapCenter;
uniform float u_snapRadius;
uniform float u_snapActive;

// Centre et rayon en unités du monde, fournis par la logique métier.
uniform vec2 u_visibleCenter;
uniform float u_visibleRadius;
uniform float u_hasVisibleArea;

void main() {
    // Un test de distance au carré évite la racine carrée et suit le héros
    // continûment, sans arrondir sa position à une case.
    vec2 offset = v_worldPosition - u_visibleCenter;
    bool visible = u_hasVisibleArea > 0.5
        && dot(offset, offset) <= u_visibleRadius * u_visibleRadius;

    float snapReveal = 0.0;
    if (u_snapActive > 0.5 && u_snapRadius > 0.0) {
        // Convertit la position du monde en coordonnées de texture (0 à 1).
        // Le carré du masque reste centré sur l'origine figée du snap.
        vec2 maskUV =
            (v_worldPosition - u_snapCenter) / (2.0 * u_snapRadius)
            + vec2(0.5);

        // Hors du carré, aucune révélation : on évite d'étirer ses bords.
        if (maskUV.x >= 0.0 && maskUV.x <= 1.0
                && maskUV.y >= 0.0 && maskUV.y <= 1.0) {
            // Le haut des PNG correspond à v = 0, le haut du monde à y positif.
            vec2 imageUV = vec2(maskUV.x, 1.0 - maskUV.y);
            // Les masques sont RGB : leur luminosité, pas leur alpha,
            // indique la révélation. Noir = 0, blanc = 1.
            snapReveal = clamp(texture2D(u_snapMask, imageUV).r, 0.0, 1.0);
        }
    }

    // Lit les marbrures du voile. Leur alpha n'est pas utilisé : en dehors
    // des zones révélées, aucune partie de la carte ne doit transparaître.
    vec3 fogColor = texture2D(u_texture, v_texCoords).rgb * v_color.rgb;
    // L'union des deux révélations préserve toujours le cercle du héros.
    float permanentReveal = visible ? 1.0 : 0.0;
    float opacity = 1.0 - max(permanentReveal, snapReveal);
    gl_FragColor = vec4(fogColor, opacity);
}
