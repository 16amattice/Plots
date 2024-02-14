package com.bgsoftware.superiorskyblock.api.plot.algorithms;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.key.Key;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface PlotCalculationAlgorithm {

    /**
     * Calculate the plot blocks of the plot.
     *
     * @return CompletableFuture instance of the result.
     * @deprecated See {@link #calculatePlot(Plot)}
     */
    @Deprecated
    default CompletableFuture<PlotCalculationResult> calculatePlot() {
        throw new UnsupportedOperationException("This method is not supported anymore. Use calculatePlot(Plot) instead.");
    }

    /**
     * Calculate the plot blocks of the plot.
     *
     * @param plot The plot to calculate blocks for.
     * @return CompletableFuture instance of the result.
     */
    CompletableFuture<PlotCalculationResult> calculatePlot(Plot plot);

    /**
     * Represents calculation result.
     */
    interface PlotCalculationResult {

        /**
         * Get all block-counts that were calculated.
         */
        Map<Key, BigInteger> getBlockCounts();

    }

}
