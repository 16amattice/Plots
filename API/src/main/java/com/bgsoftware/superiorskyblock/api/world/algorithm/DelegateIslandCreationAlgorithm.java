package com.bgsoftware.superiorskyblock.api.world.algorithm;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DelegatePlotCreationAlgorithm implements PlotCreationAlgorithm {

    protected final PlotCreationAlgorithm handle;

    protected DelegatePlotCreationAlgorithm(PlotCreationAlgorithm handle) {
        this.handle = handle;
    }

    @Override
    @Deprecated
    public CompletableFuture<PlotCreationResult> createPlot(UUID plotUUID, SuperiorPlayer owner,
                                                                BlockPosition lastPlot, String plotName,
                                                                Schematic schematic) {
        return this.handle.createPlot(plotUUID, owner, lastPlot, plotName, schematic);
    }

    @Override
    public CompletableFuture<PlotCreationResult> createPlot(Plot.Builder builder, BlockPosition lastPlot) {
        return this.handle.createPlot(builder, lastPlot);
    }

}
