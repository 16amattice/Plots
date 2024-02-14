package com.bgsoftware.superiorskyblock.core.factory;

import com.bgsoftware.superiorskyblock.api.factory.PlotsFactory;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotCalculationAlgorithm;
import com.bgsoftware.superiorskyblock.api.plot.algorithms.PlotEntitiesTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.persistence.PersistentDataContainer;

public class DefaultPlotsFactory implements PlotsFactory {

    private static final DefaultPlotsFactory INSTANCE = new DefaultPlotsFactory();

    public static DefaultPlotsFactory getInstance() {
        return INSTANCE;
    }

    private DefaultPlotsFactory() {
    }

    @Override
    public Plot createPlot(Plot original) {
        return original;
    }

    @Override
    public PlotCalculationAlgorithm createPlotCalculationAlgorithm(Plot plot, PlotCalculationAlgorithm original) {
        return original;
    }

    @Override
    public PlotBlocksTrackerAlgorithm createPlotBlocksTrackerAlgorithm(Plot plot, PlotBlocksTrackerAlgorithm original) {
        return original;
    }

    @Override
    public PlotEntitiesTrackerAlgorithm createPlotEntitiesTrackerAlgorithm(Plot plot, PlotEntitiesTrackerAlgorithm original) {
        return original;
    }

    @Override
    public PersistentDataContainer createPersistentDataContainer(Plot plot, PersistentDataContainer original) {
        return original;
    }

}
