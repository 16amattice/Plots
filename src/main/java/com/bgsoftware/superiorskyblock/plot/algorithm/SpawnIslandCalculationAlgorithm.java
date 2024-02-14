package com.bgsoftware.superiorskyblock.plot.algorithm;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotCalculationAlgorithm;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class SpawnPlotCalculationAlgorithm implements PlotCalculationAlgorithm {

    private static final SpawnPlotCalculationAlgorithm INSTANCE = new SpawnPlotCalculationAlgorithm();
    private static final PlotCalculationResult RESULT = Collections::emptyMap;

    private SpawnPlotCalculationAlgorithm() {

    }

    public static SpawnPlotCalculationAlgorithm getInstance() {
        return INSTANCE;
    }

    @Override
    public CompletableFuture<PlotCalculationResult> calculatePlot(Plot plot) {
        return CompletableFuture.completedFuture(RESULT);
    }

}
