package com.bgsoftware.superiorskyblock.api.factory;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotCalculationAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotEntitiesTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.persistence.PersistentDataContainer;

public class DelegatePlotsFactory implements PlotsFactory {

    protected final PlotsFactory handle;

    protected DelegatePlotsFactory(PlotsFactory handle) {
        this.handle = handle;
    }

    @Override
    public Plot createPlot(Plot original) {
        return this.handle.createPlot(original);
    }

    @Override
    @Deprecated
    public PlotCalculationAlgorithm createPlotCalculationAlgorithm(Plot plot) {
        return this.handle.createPlotCalculationAlgorithm(plot);
    }

    @Override
    @Deprecated
    public PlotBlocksTrackerAlgorithm createPlotBlocksTrackerAlgorithm(Plot plot) {
        return this.handle.createPlotBlocksTrackerAlgorithm(plot);
    }

    @Override
    @Deprecated
    public PlotEntitiesTrackerAlgorithm createPlotEntitiesTrackerAlgorithm(Plot plot) {
        return this.handle.createPlotEntitiesTrackerAlgorithm(plot);
    }

    @Override
    public PlotCalculationAlgorithm createPlotCalculationAlgorithm(Plot plot, PlotCalculationAlgorithm original) {
        return this.handle.createPlotCalculationAlgorithm(plot, original);
    }

    @Override
    public PlotBlocksTrackerAlgorithm createPlotBlocksTrackerAlgorithm(Plot plot, PlotBlocksTrackerAlgorithm original) {
        return this.handle.createPlotBlocksTrackerAlgorithm(plot, original);
    }

    @Override
    public PlotEntitiesTrackerAlgorithm createPlotEntitiesTrackerAlgorithm(Plot plot, PlotEntitiesTrackerAlgorithm original) {
        return this.handle.createPlotEntitiesTrackerAlgorithm(plot, original);
    }

    @Override
    public PersistentDataContainer createPersistentDataContainer(Plot plot, PersistentDataContainer original) {
        return this.handle.createPersistentDataContainer(plot, original);
    }

}
