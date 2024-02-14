package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotJoinEvent is called when a player is joining an plot as a member of that plot.
 */
public class PlotJoinEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;
    private final Cause cause;
    private boolean cancelled = false;

    /**
     * The constructor to the event.
     *
     * @param superiorPlayer The player who joined the plot as a new member.
     * @param plot         The plot that the player joined into.
     * @deprecated See {@link #PlotJoinEvent(SuperiorPlayer, Plot, Cause)}
     */
    @Deprecated
    public PlotJoinEvent(SuperiorPlayer superiorPlayer, Plot plot) {
        this(superiorPlayer, plot, Cause.INVITE);
    }

    /**
     * The constructor to the event.
     *
     * @param superiorPlayer The player who joined the plot as a new member.
     * @param plot         The plot that the player joined into.
     * @param cause          The cause of joining the plot.
     */
    public PlotJoinEvent(SuperiorPlayer superiorPlayer, Plot plot, Cause cause) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.cause = cause;
    }

    /**
     * Get the player who joined the plot as a new member.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the cause of joining the plot.
     */
    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * The cause of joining an plot.
     */
    public enum Cause {

        /**
         * The player accepted an invitation to the plot.
         */
        INVITE,

        /**
         * The player was joined due to an admin, either by `/is admin add` or `/is admin join`
         */
        ADMIN

    }

}
