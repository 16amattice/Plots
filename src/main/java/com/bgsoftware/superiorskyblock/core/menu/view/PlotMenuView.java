package com.bgsoftware.superiorskyblock.core.menu.view;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.menu.Menu;
import com.bgsoftware.superiorskyblock.api.menu.view.MenuView;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.menu.view.args.PlotViewArgs;

public class PlotMenuView extends AbstractMenuView<PlotMenuView, PlotViewArgs> {

    private final Plot plot;

    public PlotMenuView(SuperiorPlayer inventoryViewer, @Nullable MenuView<?, ?> previousMenuView,
                          Menu<PlotMenuView, PlotViewArgs> menu, PlotViewArgs args) {
        super(inventoryViewer, previousMenuView, menu);
        this.plot = args.getPlot();
    }

    public Plot getPlot() {
        return plot;
    }

}
