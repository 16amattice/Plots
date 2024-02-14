package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotRenameWarpEvent is called when renaming a warp.
 */
public class PlotRenameWarpEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final PlotWarp plotWarp;

    private String warpName;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that renamed the warp.
     * @param plot         The plot of the warp.
     * @param plotWarp     The warp that was renamed.
     * @param warpName       The new name of the warp.
     */
    public PlotRenameWarpEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotWarp plotWarp, String warpName) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotWarp = plotWarp;
        this.warpName = warpName;
    }

    /**
     * Get the player that renamed the warp.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp that was renamed.
     */
    public PlotWarp getPlotWarp() {
        return plotWarp;
    }

    /**
     * Get the new name of the warp.
     */
    public String getWarpName() {
        return warpName;
    }

    /**
     * Set the new name for the warp.
     *
     * @param warpName The new warp name to set.
     */
    public void setWarpName(String warpName) {
        Preconditions.checkNotNull(warpName, "Cannot set warp name to null.");
        Preconditions.checkArgument(warpName.length() <= 255, "Warp names cannot be longer than 255 chars.");
        Preconditions.checkState(plot.getWarp(warpName) == null, "Cannot rename warps to an already existing warps.");

        this.warpName = warpName;
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
