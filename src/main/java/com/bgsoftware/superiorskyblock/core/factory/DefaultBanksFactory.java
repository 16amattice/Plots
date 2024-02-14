package com.bgsoftware.superiorskyblock.core.factory;

import com.bgsoftware.superiorskyblock.api.factory.BanksFactory;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.bank.PlotBank;

public class DefaultBanksFactory implements BanksFactory {

    private static final DefaultBanksFactory INSTANCE = new DefaultBanksFactory();

    public static DefaultBanksFactory getInstance() {
        return INSTANCE;
    }

    private DefaultBanksFactory() {
    }

    @Override
    public PlotBank createPlotBank(Plot plot, PlotBank original) {
        return original;
    }

}
