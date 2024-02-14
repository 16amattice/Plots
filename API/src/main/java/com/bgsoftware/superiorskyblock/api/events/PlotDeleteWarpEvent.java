package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.warps.PlotWarp;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotDeleteWarpEvent is called when a warp is deleted from an plot.
 */
public class PlotDeleteWarpEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final PlotWarp plotWarp;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that deleted the warp.
     *                       If null, then the warp was deleted by the console.
     * @param plot         The plot that the warp was deleted from.
     * @param plotWarp     The warp that was deleted.
     */
    public PlotDeleteWarpEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, PlotWarp plotWarp) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotWarp = plotWarp;
    }

    /**
     * Get the player that deleted the warp.
     * If null, then the warp was deleted by the console or not by a command.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the warp that was deleted.
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
