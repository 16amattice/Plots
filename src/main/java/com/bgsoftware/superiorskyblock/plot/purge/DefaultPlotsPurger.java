package com.bgsoftware.superiorskyblock.plot.purge;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultPlotsPurger implements PlotsPurger {

    private final Set<Plot> scheduledPlots = new HashSet<>();

    @Override
    public void schedulePlotPurge(Plot plot) {
        this.scheduledPlots.add(plot);
    }

    @Override
    public void unschedulePlotPurge(Plot plot) {
        this.scheduledPlots.remove(plot);
    }

    @Override
    public boolean isPlotPurgeScheduled(Plot plot) {
        return this.scheduledPlots.contains(plot);
    }

    @Override
    public List<Plot> getScheduledPurgedPlots() {
        return new SequentialListBuilder<Plot>().build(this.scheduledPlots);
    }

}
