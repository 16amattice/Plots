package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotQuitEvent is called when a player is leaving their plot.
 */
public class PlotQuitEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who left their plot.
     * @param plot         The plot that the player left.
     */
    public PlotQuitEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the player who left their plot.
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
