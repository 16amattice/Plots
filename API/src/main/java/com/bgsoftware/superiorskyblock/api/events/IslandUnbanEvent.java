package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotUnbanEvent is called when a player is unbanned from his plot.
 */
public class PlotUnbanEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final SuperiorPlayer targetPlayer;

    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player who unbanned the other player.
     * @param targetPlayer   The player that was unbanned.
     * @param plot         The plot that the player was unbanned from.
     */
    public PlotUnbanEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.targetPlayer = targetPlayer;
    }

    /**
     * Get the player who unbanned the other player.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the player that was unbanned.
     */
    public SuperiorPlayer getUnbannedPlayer() {
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
