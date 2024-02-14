package com.bgsoftware.superiorskyblock.commands.arguments;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.List;

public class PlotsListArgument extends Argument<List<Plot>, SuperiorPlayer> {

    public PlotsListArgument(List<Plot> plots, @Nullable SuperiorPlayer superiorPlayer) {
        super(plots, superiorPlayer);
    }

    public List<Plot> getPlots() {
        return super.k;
    }

    @Nullable
    public SuperiorPlayer getSuperiorPlayer() {
        return super.v;
    }

}
