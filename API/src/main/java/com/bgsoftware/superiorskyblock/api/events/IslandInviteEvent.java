package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotInviteEvent is called when a player is invited to an plot.
 */
public class PlotInviteEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final SuperiorPlayer targetPlayer;
    private boolean cancelled = false;

    /**
     * The constructor for the event.
     *
     * @param superiorPlayer The player who sent the invite request.
     * @param targetPlayer   The player who received the invite request.
     * @param plot         The plot that the player was invited into.
     */
    public PlotInviteEvent(SuperiorPlayer superiorPlayer, SuperiorPlayer targetPlayer, Plot plot) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.targetPlayer = targetPlayer;
    }

    /**
     * Get the player who sent the invite request.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the player who received the invite request.
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
