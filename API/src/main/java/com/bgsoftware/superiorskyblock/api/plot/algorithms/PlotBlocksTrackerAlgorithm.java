package com.bgsoftware.superiorskyblock.api.plot.algorithms;

import com.bgsoftware.superiorskyblock.api.key.Key;

import java.math.BigInteger;
import java.util.Map;

public interface PlotBlocksTrackerAlgorithm {

    /**
     * Track a new block with a specific amount.
     *
     * @param key    The block's key that should be tracked.
     * @param amount The amount of the block.
     * @return Whether the block was successfully tracked.
     */
    boolean trackBlock(Key key, BigInteger amount);

    /**
     * Untrack a block with a specific amount.
     *
     * @param key    The block's key that should be untracked.
     * @param amount The amount of the block.
     * @return Whether the block was successfully untracked.
     */
    boolean untrackBlock(Key key, BigInteger amount);

    /**
     * Get the amount of blocks that are on the plot.
     *
     * @param key The block's key to check.
     */
    BigInteger getBlockCount(Key key);

    /**
     * Get the amount of blocks that are on the plot.
     * Unlike getBlockCount(Key), this method returns the count for
     * the exactly block that is given as a parameter.
     *
     * @param key The block's key to check.
     */
    BigInteger getExactBlockCount(Key key);

    /**
     * Get all the blocks that are on the plot.
     */
    Map<Key, BigInteger> getBlockCounts();

    /**
     * Clear all the block counts of the plot.
     */
    void clearBlockCounts();

    /**
     * Set whether the tracker should be in "loading data mode" or not.
     * When loading mode is enabled, the tracker should only track blocks for the given blocks
     * and not their variants (limits, global blocks, etc)
     *
     * @param loadingDataMode loading mode
     */
    void setLoadingDataMode(boolean loadingDataMode);

}
