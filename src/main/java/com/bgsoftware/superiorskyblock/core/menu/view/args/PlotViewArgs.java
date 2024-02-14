package com.bgsoftware.superiorskyblock.core.menu.view.args;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.view.ViewArgs;

public class PlotViewArgs implements ViewArgs {

    private final Plot plot;

    public PlotViewArgs(Plot plot) {
        this.plot = plot;
    }

    public Plot getPlot() {
        return plot;
    }

}
