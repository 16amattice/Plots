package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeCoopLimitEvent is called when the coop-limit of the plot is changed.
 */
public class PlotChangeCoopLimitEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private int coopLimit;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the coop limit of the plot.
     *                       If set to null, it means the limit was changed via the console.
     * @param plot         The plot that the coop limit was changed for.
     * @param coopLimit      The new coop limit of the plot
     */
    public PlotChangeCoopLimitEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, int coopLimit) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.coopLimit = coopLimit;
    }

    /**
     * Get the player that changed the coop limit.
     * If null, it means the limit was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new coop limit of the plot.
     */
    public int getCoopLimit() {
        return coopLimit;
    }

    /**
     * Set the new coop limit for the plot.
     *
     * @param coopLimit The new coop limit to set.
     */
    public void setCoopLimit(int coopLimit) {
        Preconditions.checkArgument(coopLimit >= 0, "Cannot set the coop limit to a negative limit.");
        this.coopLimit = coopLimit;
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
