package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotCloseEvent is called when the plot is closed for visitors.
 */
public class PlotCloseEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that closed the plot.
     *                       If null, then the plot was opened by console.
     * @param plot         The plot that was closed.
     */
    public PlotCloseEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the player that closed the plot.
     * If null, then the plot was opened by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
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
