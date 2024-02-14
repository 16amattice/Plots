package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.HandlerList;

/**
 * PlotEnterProtectedEvent is called when a player is walking into an plot's protected area.
 * The protected area is the area that players can build in.
 */
public class PlotEnterProtectedEvent extends PlotEnterEvent {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who entered to the plot's area.
     * @param plot         The plot that the player entered into.
     * @param enterCause     The cause of entering into the plot.
     */
    public PlotEnterProtectedEvent(SuperiorPlayer superiorPlayer, Plot plot, EnterCause enterCause) {
        super(superiorPlayer, plot, enterCause);
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
