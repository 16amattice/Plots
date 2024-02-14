package com.bgsoftware.superiorskyblock.api.factory;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotCalculationAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotEntitiesTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.persistence.PersistentDataContainer;

public interface PlotsFactory {

    /**
     * Create a new plot.
     *
     * @param original The original plot that was created.
     */
    Plot createPlot(Plot original);

    /**
     * Create a calculation algorithm for an plot.
     *
     * @param plot The plot to set the algorithm to.
     * @deprecated Use {@link #createPlotCalculationAlgorithm(Plot, PlotCalculationAlgorithm)}
     */
    @Deprecated
    default PlotCalculationAlgorithm createPlotCalculationAlgorithm(Plot plot) {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    /**
     * Create a blocks-tracking algorithm for an plot.
     *
     * @param plot The plot to set the algorithm to.
     * @deprecated Use {@link #createPlotBlocksTrackerAlgorithm(Plot, PlotBlocksTrackerAlgorithm)}
     */
    @Deprecated
    default PlotBlocksTrackerAlgorithm createPlotBlocksTrackerAlgorithm(Plot plot) {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    /**
     * Create an entities-tracking algorithm for an plot.
     *
     * @param plot The plot to set the algorithm to.
     * @deprecated Use {@link #createPlotEntitiesTrackerAlgorithm(Plot, PlotEntitiesTrackerAlgorithm)}
     */
    @Deprecated
    default PlotEntitiesTrackerAlgorithm createPlotEntitiesTrackerAlgorithm(Plot plot) {
        throw new UnsupportedOperationException("Unsupported operation.");
    }

    /**
     * Create a calculation algorithm for an plot.
     *
     * @param plot   The plot to set the algorithm to.
     * @param original The original calculation algorithm.
     */
    PlotCalculationAlgorithm createPlotCalculationAlgorithm(Plot plot, PlotCalculationAlgorithm original);

    /**
     * Create a blocks-tracking algorithm for an plot.
     *
     * @param plot   The plot to set the algorithm to.
     * @param original The original blocks tracking algorithm.
     */
    PlotBlocksTrackerAlgorithm createPlotBlocksTrackerAlgorithm(Plot plot, PlotBlocksTrackerAlgorithm original);

    /**
     * Create an entities-tracking algorithm for an plot.
     *
     * @param plot   The plot to set the algorithm to.
     * @param original The original entities tracking algorithm.
     */
    PlotEntitiesTrackerAlgorithm createPlotEntitiesTrackerAlgorithm(Plot plot, PlotEntitiesTrackerAlgorithm original);

    /**
     * Create a new persistent data container for an plot.
     *
     * @param plot   The plot to create the container for.
     * @param original The original persistent data container that was created.
     */
    PersistentDataContainer createPersistentDataContainer(Plot plot, PersistentDataContainer original);

}
