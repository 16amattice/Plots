package com.bgsoftware.superiorskyblock.api.factory;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.bank.PlotBank;

public interface BanksFactory {

    /**
     * Create a new bank for an plot.
     *
     * @param plot   The plot to create the bank for.
     * @param original The original plot bank that was created.
     */
    PlotBank createPlotBank(Plot plot, PlotBank original);

}
