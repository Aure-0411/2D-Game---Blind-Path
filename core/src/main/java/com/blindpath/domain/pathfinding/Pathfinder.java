package com.blindpath.domain.pathfinding;

import java.util.List;

public interface Pathfinder {

    List<GridPosition> findPath(LabyrinthGrid grid, GridPosition start, GridPosition target);

}