package com.bgsoftware.superiorskyblock.api.world.algorithm;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlotCreationAlgorithm {

    /**
     * Create a new plot on the server.
     * This method should not only create the Plot object itself, but also paste a schematic.
     * Teleportation and plot initialization will be handled by the plugin.
     *
     * @param plotUUID The uuid of the plot.
     * @param owner      The owner of the plot.
     * @param lastPlot The location of the last generated plot.
     * @param plotName The name of the plot.
     * @param schematic  The schematic used to create the plot.
     */
    CompletableFuture<PlotCreationResult> createPlot(UUID plotUUID, SuperiorPlayer owner, BlockPosition lastPlot,
                                                         String plotName, Schematic schematic);

    /**
     * Create a new plot on the server.
     * This method should not only create the Plot object itself, but also paste a schematic.
     * Teleportation and plot initialization will be handled by the plugin.
     *
     * @param builder    The builder of the plot.
     * @param lastPlot The location of the last generated plot.
     */
    CompletableFuture<PlotCreationResult> createPlot(Plot.Builder builder, BlockPosition lastPlot);

    /**
     * Class representing result of a creation process.
     */
    class PlotCreationResult {

        private final Status status;
        private final Plot plot;
        private final Location plotLocation;
        private final boolean shouldTeleportPlayer;

        /**
         * Constructor of the result.
         *
         * @param plot               The created plot.
         * @param plotLocation       The location of the plot.
         * @param shouldTeleportPlayer Whether to teleport the player to his plot or not.
         * @deprecated See {@link #PlotCreationResult(Status, Plot, Location, boolean)}
         */
        @Deprecated
        public PlotCreationResult(Plot plot, Location plotLocation, boolean shouldTeleportPlayer) {
            this(Status.SUCCESS, plot, plotLocation, shouldTeleportPlayer);
        }

        /**
         * Constructor of the result.
         *
         * @param status               The status of the creation result.
         *                             In case of failure, the rest of the parameters are undefined.
         * @param plot               The created plot.
         * @param plotLocation       The location of the plot.
         * @param shouldTeleportPlayer Whether to teleport the player to his plot or not.
         */
        public PlotCreationResult(Status status, Plot plot, Location plotLocation, boolean shouldTeleportPlayer) {
            this.status = status;
            this.plot = plot;
            this.plotLocation = plotLocation;
            this.shouldTeleportPlayer = shouldTeleportPlayer;
        }

        /**
         * Get the status of the creation task.
         */
        public Status getStatus() {
            return status;
        }

        /**
         * Get the created plot object.
         */
        public Plot getPlot() {
            Preconditions.checkState(this.getStatus() == Status.SUCCESS, "Result is not successful.");
            return plot;
        }

        /**
         * Get the location of the new plot.
         */
        public Location getPlotLocation() {
            Preconditions.checkState(this.getStatus() == Status.SUCCESS, "Result is not successful.");
            return plotLocation;
        }

        /**
         * Get whether the player that created the plot should be teleported to it.
         */
        public boolean shouldTeleportPlayer() {
            Preconditions.checkState(this.getStatus() == Status.SUCCESS, "Result is not successful.");
            return shouldTeleportPlayer;
        }

        public enum Status {

            NAME_OCCUPIED,
            SUCCESS

        }

    }

}
