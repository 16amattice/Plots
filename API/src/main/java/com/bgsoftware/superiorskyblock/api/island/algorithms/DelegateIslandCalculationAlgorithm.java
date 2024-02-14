package com.bgsoftware.superiorskyblock.api.plot.algorithms;

import com.bgsoftware.superiorskyblock.api.plot.Plot;

import java.util.concurrent.CompletableFuture;

public class DelegatePlotCalculationAlgorithm implements PlotCalculationAlgorithm {

    protected final PlotCalculationAlgorithm handle;

    protected DelegatePlotCalculationAlgorithm(PlotCalculationAlgorithm handle) {
        this.handle = handle;
    }

    @Override
    @Deprecated
    public CompletableFuture<PlotCalculationResult> calculatePlot() {
        return this.handle.calculatePlot();
    }

    @Override
    public CompletableFuture<PlotCalculationResult> calculatePlot(Plot plot) {
        return this.handle.calculatePlot(plot);
    }

}
