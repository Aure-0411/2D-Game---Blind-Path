package com.blindpath.domain.light;

public class SnapEvent {

    private final float sourceX;
    private final float sourceY;
    private final SnapSource source;
    private final long groupId;

    public SnapEvent(float sourceX, float sourceY, SnapSource source, long groupId) {

        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.source = source;
        this.groupId = groupId;
    
    }

    public float getSourceX() {
        return sourceX;
    }

    public float getSourceY() {
        return sourceY;
    }

    public SnapSource getSource() {
        return source;
    }

    public long getGroupId() {
        return groupId;
    }


}