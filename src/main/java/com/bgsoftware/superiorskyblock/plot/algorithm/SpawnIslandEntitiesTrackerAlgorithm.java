package com.bgsoftware.superiorskyblock.plot.algorithm;

import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotEntitiesTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.key.Key;

import java.util.Collections;
import java.util.Map;

public class SpawnPlotEntitiesTrackerAlgorithm implements PlotEntitiesTrackerAlgorithm {

    private static final SpawnPlotEntitiesTrackerAlgorithm INSTANCE = new SpawnPlotEntitiesTrackerAlgorithm();

    private SpawnPlotEntitiesTrackerAlgorithm() {
    }

    public static SpawnPlotEntitiesTrackerAlgorithm getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean trackEntity(Key key, int amount) {
        return false;
    }

    @Override
    public boolean untrackEntity(Key key, int amount) {
        return false;
    }

    @Override
    public int getEntityCount(Key key) {
        return 0;
    }

    @Override
    public Map<Key, Integer> getEntitiesCounts() {
        return Collections.emptyMap();
    }

    @Override
    public void clearEntityCounts() {
        // Do nothing.
    }

    @Override
    public void recalculateEntityCounts() {
        // Do nothing.
    }

    @Override
    public boolean canRecalculateEntityCounts() {
        return false;
    }

}
