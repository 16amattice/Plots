package com.bgsoftware.superiorskyblock.plot.algorithm;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.data.DatabaseBridgeMode;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.world.algorithm.PlotCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.core.Text;
import com.bgsoftware.superiorskyblock.core.events.EventResult;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.core.profiler.ProfileType;
import com.bgsoftware.superiorskyblock.core.profiler.Profiler;
import com.bgsoftware.superiorskyblock.plot.builder.PlotBuilderImpl;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultPlotCreationAlgorithm implements PlotCreationAlgorithm {

    private static final DefaultPlotCreationAlgorithm INSTANCE = new DefaultPlotCreationAlgorithm();

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private DefaultPlotCreationAlgorithm() {

    }

    public static DefaultPlotCreationAlgorithm getInstance() {
        return INSTANCE;
    }

    @Override
    public CompletableFuture<PlotCreationResult> createPlot(UUID plotUUID, SuperiorPlayer owner,
                                                                BlockPosition lastPlot, String plotName,
                                                                Schematic schematic) {
        Preconditions.checkNotNull(plotUUID, "plotUUID parameter cannot be null.");
        Preconditions.checkNotNull(owner, "owner parameter cannot be null.");
        Preconditions.checkNotNull(lastPlot, "lastPlot parameter cannot be null.");
        Preconditions.checkNotNull(plotName, "plotName parameter cannot be null.");
        Preconditions.checkNotNull(schematic, "schematic parameter cannot be null.");
        return createPlot(Plot.newBuilder()
                        .setOwner(owner)
                        .setUniqueId(plotUUID)
                        .setName(plotName)
                        .setSchematicName(schematic.getName())
                , lastPlot);
    }

    @Override
    public CompletableFuture<PlotCreationResult> createPlot(Plot.Builder builderParam, BlockPosition lastPlot) {
        Preconditions.checkNotNull(builderParam, "builder parameter cannot be null.");
        Preconditions.checkArgument(builderParam instanceof PlotBuilderImpl, "Cannot create an plot from custom builder.");
        Preconditions.checkNotNull(lastPlot, "lastPlot parameter cannot be null.");

        PlotBuilderImpl builder = (PlotBuilderImpl) builderParam;

        Schematic schematic = builder.plotType == null ? null : plugin.getSchematics().getSchematic(builder.plotType);

        Preconditions.checkArgument(builder.owner != null, "Cannot create an plot from builder with no valid owner.");
        Preconditions.checkArgument(schematic != null, "Cannot create an plot from builder with invalid schematic name.");

        Log.debug(Debug.CREATE_PLOT, builder.owner.getName(), schematic.getName(), lastPlot);

        // Making sure an plot with the same name does not exist.
        if (!Text.isBlank(builder.plotName) && plugin.getGrid().getPlot(builder.plotName) != null) {
            Log.debugResult(Debug.CREATE_PLOT, "Creation Failed", "Plot with the name " + builder.plotName + " already exists.");
            return CompletableFuture.completedFuture(new PlotCreationResult(PlotCreationResult.Status.NAME_OCCUPIED, null, null, false));
        }

        long profiler = Profiler.start(ProfileType.CREATE_PLOT);

        CompletableFuture<PlotCreationResult> completableFuture = new CompletableFuture<>();

        Location plotLocation = plugin.getProviders().getWorldsProvider().getNextLocation(
                lastPlot.parse().clone(),
                plugin.getSettings().getPlotHeight(),
                plugin.getSettings().getMaxPlotSize(),
                builder.owner.getUniqueId(),
                builder.uuid
        );

        Log.debugResult(Debug.CREATE_PLOT, "Next Plot Position", plotLocation);

        Plot plot = builder.setCenter(plotLocation.add(0.5, 0, 0.5)).build();

        plot.getDatabaseBridge().setDatabaseBridgeMode(DatabaseBridgeMode.IDLE);

        EventResult<Boolean> event = plugin.getEventsBus().callPlotCreateEvent(builder.owner, plot, builder.plotType);

        if (!event.isCancelled()) {
            schematic.pasteSchematic(plot, plotLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), () -> {
                plugin.getProviders().getWorldsProvider().finishPlotCreation(plotLocation,
                        builder.owner.getUniqueId(), builder.uuid);
                completableFuture.complete(new PlotCreationResult(PlotCreationResult.Status.SUCCESS, plot, plotLocation, event.getResult()));
                plot.getDatabaseBridge().setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
                Profiler.end(profiler);
            }, error -> {
                plot.getDatabaseBridge().setDatabaseBridgeMode(DatabaseBridgeMode.SAVE_DATA);
                plugin.getProviders().getWorldsProvider().finishPlotCreation(plotLocation,
                        builder.owner.getUniqueId(), builder.uuid);
                completableFuture.completeExceptionally(error);
                Profiler.end(profiler);
            });
        }

        return completableFuture;
    }


}
