package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotBanEvent is called when a player is banned from his plot.
 */
public class PlotBanEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final SuperiorPlayer targetPlayer;

    private boolean cancelled;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who banned the other player.
     * @param targetPlayer   The player that was banned.
     * @param plot         The plot that the player was banned from.
     */
    public PlotBanEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.targetPlayer = targetPlayer;
    }

    /**
     * Get the player who banned the other player.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the player that was banned.
     */
    public SuperiorPlayer getTarget() {
        return targetPlayer;
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
