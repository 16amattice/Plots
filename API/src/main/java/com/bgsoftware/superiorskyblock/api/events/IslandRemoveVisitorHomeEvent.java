package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

/**
 * PlotRemoveVisitorHomeEvent is called when the visitor home of the plot is removed.
 */
public class PlotRemoveVisitorHomeEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that removed the plot visitor home.
     * @param plot         The plot that the visitor home was removed.
     */
    public PlotRemoveVisitorHomeEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
    }

    /**
     * Get the player who removed the plot visitor home.
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
