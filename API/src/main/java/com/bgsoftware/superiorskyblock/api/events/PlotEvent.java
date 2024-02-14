package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * All the plot events extend PlotEvent.
 */
public abstract class PlotEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    protected final Plot plot;

    /**
     * The constructor for the event.
     *
     * @param plot The plot object that was involved in the event.
     */
    public PlotEvent(Plot plot) {
        super(!Bukkit.isPrimaryThread());
        this.plot = plot;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the plot that was involved in the event.
     */
    public Plot getPlot() {
        return plot;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
