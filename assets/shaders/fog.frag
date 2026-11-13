#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_worldPosition;

// SpriteBatch associe automatiquement la texture dessinée à u_texture.
uniform sampler2D u_texture;

// Coin inférieur gauche de la case visible, choisi côté métier.
// Le code Java devra fournir ces valeurs avant de dessiner le voile.
uniform vec2 u_visibleCell;
// 0 = tout masquer ; 1 = ouvrir la case. Évite une ouverture par défaut en (0, 0).
uniform float u_hasVisibleCell;

void main() {
    // Chaque case mesure une unité. floor donne l'indice de la case du pixel.
    vec2 cell = floor(v_worldPosition);
    bool visible = u_hasVisibleCell > 0.5
        && cell.x == u_visibleCell.x
        && cell.y == u_visibleCell.y;

    // Lit les marbrures du voile. Leur alpha n'est pas utilisé : en dehors
    // de la case visible, aucune partie de la carte ne doit transparaître.
    vec3 fogColor = texture2D(u_texture, v_texCoords).rgb * v_color.rgb;
    float opacity = visible ? 0.0 : 1.0;
    gl_FragColor = vec4(fogColor, opacity);
}
