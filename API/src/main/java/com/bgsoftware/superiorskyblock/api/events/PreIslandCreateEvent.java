package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * PrePlotCreateEvent is called when a new plot is created.
 */
public class PrePlotCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final SuperiorPlayer superiorPlayer;
    private final String plotName;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who created the plot.
     * @param plotName     The name that was given to the plot.
     */
    public PrePlotCreateEvent(SuperiorPlayer superiorPlayer, String plotName) {
        this.superiorPlayer = superiorPlayer;
        this.plotName = plotName;
    }

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who created the plot.
     * @deprecated See PrePlotCreateEvent(SuperiorPlayer, String)
     */
    @Deprecated
    public PrePlotCreateEvent(SuperiorPlayer superiorPlayer) {
        this(superiorPlayer, "");
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the player who created the plot.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the name that was given to the plot.
     */
    public String getPlotName() {
        return plotName;
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
