package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeDiscordEvent is called when the discord of the plot is changed.
 */
public class PlotChangeDiscordEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;

    private String discord;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the discord of the plot.
     * @param plot         The plot that the discord was changed for.
     * @param discord        The new discord of the plot
     */
    public PlotChangeDiscordEvent(SuperiorPlayer superiorPlayer, Plot plot, String discord) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.discord = discord;
    }

    /**
     * Get the player that changed the discord of the plot.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new discord of the plot.
     */
    public String getDiscord() {
        return discord;
    }

    /**
     * Set the new discord for the plot.
     *
     * @param discord The new discord to set.
     */
    public void setDiscord(String discord) {
        Preconditions.checkNotNull(discord, "Cannot set the discord of the plot to null.");
        this.discord = discord;
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
