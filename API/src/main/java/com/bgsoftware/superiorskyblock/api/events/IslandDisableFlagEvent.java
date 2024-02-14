package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.plot.PlotFlag;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotDisableFlagEvent is called when a flag is disabling for an plot.
 */
public class PlotDisableFlagEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;
    private final PlotFlag plotFlag;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that disabled the plot flag for the plot.
     *                       If null, the flag was disabled by console.
     * @param plot         The plot that the flag was disabled for.
     * @param plotFlag     The flag that was disabled.
     */
    public PlotDisableFlagEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, PlotFlag plotFlag) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.plotFlag = plotFlag;
    }

    /**
     * Get the player that disabled the plot flag for the plot.
     * If null, the flag was disabled by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the flag that was disabled.
     */
    public PlotFlag getPlotFlag() {
        return plotFlag;
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
