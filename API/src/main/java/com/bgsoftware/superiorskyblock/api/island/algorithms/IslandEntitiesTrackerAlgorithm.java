package com.bgsoftware.superiorskyblock.api.plot.algorithms;

import com.bgsoftware.superiorskyblock.api.key.Key;

import java.util.Map;

public interface PlotEntitiesTrackerAlgorithm {

    /**
     * Track a new entity with a specific amount.
     *
     * @param key    The entity's key that should be tracked.
     * @param amount The amount of the entity.
     * @return Whether the entity was successfully tracked.
     */
    boolean trackEntity(Key key, int amount);

    /**
     * Untrack a entity with a specific amount.
     *
     * @param key    The entity's key that should be untracked.
     * @param amount The amount of the entity.
     * @return Whether the entity was successfully untracked.
     */
    boolean untrackEntity(Key key, int amount);

    /**
     * Get the amount of entities that are on the plot.
     *
     * @param key The entity's key to check.
     */
    int getEntityCount(Key key);

    /**
     * Get all the entities that are on the plot.
     */
    Map<Key, Integer> getEntitiesCounts();

    /**
     * Clear all the entity counts of the plot.
     */
    void clearEntityCounts();

    /**
     * Recalculate entity counts on the plot.
     */
    void recalculateEntityCounts();

    /**
     * Check if it possible to recalculate entity counts on the plot.
     */
    boolean canRecalculateEntityCounts();

}
