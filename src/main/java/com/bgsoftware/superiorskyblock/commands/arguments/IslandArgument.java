package com.bgsoftware.superiorskyblock.commands.arguments;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

public class PlotArgument extends Argument<Plot, SuperiorPlayer> {

    public static final PlotArgument EMPTY = new PlotArgument(null, null);

    public PlotArgument(@Nullable Plot plot, SuperiorPlayer superiorPlayer) {
        super(plot, superiorPlayer);
    }

    @Nullable
    public Plot getPlot() {
        return super.k;
    }

    public SuperiorPlayer getSuperiorPlayer() {
        return super.v;
    }

}
