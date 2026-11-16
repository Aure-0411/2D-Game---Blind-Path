package com.blindpath.domain.monster;

import com.blindpath.domain.pathfinding.GridPosition;
import com.blindpath.domain.pathfinding.LabyrinthGrid;

public interface MovementStrategy {
    GridPosition nextStep(LabyrinthGrid grid, GridPosition from, GridPosition target);
}