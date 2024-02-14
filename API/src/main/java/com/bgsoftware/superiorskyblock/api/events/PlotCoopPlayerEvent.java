package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotCoopPlayerEvent is called when a player is making another player coop on their plot.
 */
public class PlotCoopPlayerEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer player;
    private final SuperiorPlayer target;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot The plot that the leadership of it is transferred.
     * @param player The player who cooped the target.
     * @param target The player that will be cooped.
     */
    public PlotCoopPlayerEvent(Plot plot, SuperiorPlayer player, SuperiorPlayer target) {
        super(plot);
        this.player = player;
        this.target = target;
    }

    /**
     * Get the player who cooped the target.
     */
    public SuperiorPlayer getPlayer() {
        return player;
    }

    /**
     * Get the player that will be cooped.
     */
    public SuperiorPlayer getTarget() {
        return target;
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