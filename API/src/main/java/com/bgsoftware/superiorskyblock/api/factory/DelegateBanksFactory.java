package com.bgsoftware.superiorskyblock.api.factory;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.bank.PlotBank;

public class DelegateBanksFactory implements BanksFactory {

    protected final BanksFactory handle;

    protected DelegateBanksFactory(BanksFactory handle) {
        this.handle = handle;
    }

    @Override
    public PlotBank createPlotBank(Plot plot, PlotBank original) {
        return this.handle.createPlotBank(plot, original);
    }

}
