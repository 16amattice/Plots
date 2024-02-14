package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotOpenWarpEvent is called when opening the warp to the public.
 */
public class PlotOpenWarpEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final PlotWarp plotWarp;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that opened the warp to the public.
     * @param plot         The plot of the warp.
     * @param plotWarp     The warp that was opened.
     */
    public PlotOpenWarpEvent(SuperiorPlayer superiorPlayer, Plot plot, PlotWarp plotWarp) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotWarp = plotWarp;
    }

    /**
     * Get the player that opened the warp to the public.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp that was opened.
     */
    public PlotWarp getPlotWarp() {
        return plotWarp;
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
