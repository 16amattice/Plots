package com.bgsoftware.superiorskyblock.plot.purge;

import com.bgsoftware.superiorskyblock.api.plot.Plot;

import java.util.List;

public interface PlotsPurger {

    void schedulePlotPurge(Plot plot);

    void unschedulePlotPurge(Plot plot);

    boolean isPlotPurgeScheduled(Plot plot);

    List<Plot> getScheduledPurgedPlots();

}
