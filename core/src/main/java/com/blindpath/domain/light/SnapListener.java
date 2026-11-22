package com.blindpath.domain.light;

/** Ce qui réagit à un claquement de doigts (patron Observer). */
public interface SnapListener {
/**
 * Appelé quand un claquement a lieu.
 *
 * @param event le claquement
 */
void onSnap(SnapEvent event);
}