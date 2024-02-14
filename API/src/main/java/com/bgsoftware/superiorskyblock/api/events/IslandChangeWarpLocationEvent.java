package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeWarpLocationEvent is called when the location of a warp was changed.
 */
public class PlotChangeWarpLocationEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final PlotWarp plotWarp;

    private Location location;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the location of the warp.
     * @param plot         The plot of the warp.
     * @param plotWarp     The warp that its location was changed.
     * @param location       The new location of the warp.
     */
    public PlotChangeWarpLocationEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotWarp plotWarp, Location location) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotWarp = plotWarp;
        this.location = location.clone();
    }

    /**
     * Get the player that changed the location of the warp.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp that its location was changed.
     */
    public PlotWarp getPlotWarp() {
        return plotWarp;
    }

    /**
     * Get the new location of the warp.
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Set the new location for the warp.
     *
     * @param location The new location to set.
     */
    public void setLocation(Location location) {
        Preconditions.checkNotNull(location, "Cannot set warp location to null.");
        Preconditions.checkNotNull(location.getWorld(), "location's world cannot be null.");
        Preconditions.checkState(plot.isInsideRange(location), "Warp locations must be inside the plot's area.");

        this.location = location.clone();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
