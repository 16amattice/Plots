package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

/**
 * PlotLeaveProtectedEvent is called when a player is walking out from the plot's protected area.
 * The protected area is the area that players can build in.
 */
public class PlotLeaveProtectedEvent extends PlotLeaveEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who left the plot's protected area.
     * @param plot         The plot that the player left.
     * @param leaveCause     The cause of leaving the plot.
     * @param toLocation     The location the player will be at after leaving.
     */
    public PlotLeaveProtectedEvent(SuperiorPlayer superiorPlayer, Plot plot, LeaveCause leaveCause, Location toLocation) {
        super(superiorPlayer, plot, leaveCause, toLocation);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
