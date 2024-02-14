package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotDisbandEvent is called when an plot is disbanded.
 */
public class PlotDisbandEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private boolean cancelled = false;

    /**
     * The constructor for the event.
     *
     * @param superiorPlayer The player who proceed the operation.
     * @param plot         The plot that is being disbanded.
     */
    public PlotDisbandEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the player who proceed the operation.
     */
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
