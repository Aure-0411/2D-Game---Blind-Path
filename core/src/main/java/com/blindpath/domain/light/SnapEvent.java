package com.blindpath.domain.light;

/**
 * Un claquement de doigts (ou une explosion) : sa position et sa source.
 * Il est diffusé à tout ce qui y réagit (monstre, ronces, leviers, coffres).
 */
public class SnapEvent {

    private final float sourceX;
    private final float sourceY;
    private final SnapSource source;
    private final long groupId;

    /**
     * @param sourceX position horizontale du claquement
     * @param sourceY position verticale du claquement
     * @param source ce qui a produit le claquement
     * @param groupId identifiant commun aux claquements produits ensemble
     */
    public SnapEvent(float sourceX, float sourceY, SnapSource source, long groupId) {

        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.source = source;
        this.groupId = groupId;
    
    }

    /** @return la position horizontale du claquement */
    public float getSourceX() {
        return sourceX;
    }

    /** @return la position verticale du claquement */
    public float getSourceY() {
        return sourceY;
    }

    /** @return ce qui a produit le claquement */
    public SnapSource getSource() {
        return source;
    }

    /** @return l'identifiant du groupe de claquements */
    public long getGroupId() {
        return groupId;
    }


}