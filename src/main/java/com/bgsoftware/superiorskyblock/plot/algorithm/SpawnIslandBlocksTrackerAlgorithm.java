package com.bgsoftware.superiorskyblock.plot.algorithm;

import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.key.Key;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class SpawnPlotBlocksTrackerAlgorithm implements PlotBlocksTrackerAlgorithm {

    private static final SpawnPlotBlocksTrackerAlgorithm INSTANCE = new SpawnPlotBlocksTrackerAlgorithm();

    private SpawnPlotBlocksTrackerAlgorithm() {
    }

    public static SpawnPlotBlocksTrackerAlgorithm getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean trackBlock(Key key, BigInteger amount) {
        return false;
    }

    @Override
    public boolean untrackBlock(Key key, BigInteger amount) {
        return false;
    }

    @Override
    public BigInteger getBlockCount(Key key) {
        return BigInteger.ZERO;
    }

    @Override
    public BigInteger getExactBlockCount(Key key) {
        return BigInteger.ZERO;
    }

    @Override
    public Map<Key, BigInteger> getBlockCounts() {
        return Collections.emptyMap();
    }

    @Override
    public void clearBlockCounts() {
        // Do nothing.
    }

    @Override
    public void setLoadingDataMode(boolean loadingDataMode) {
        // Do nothing.
    }

}
