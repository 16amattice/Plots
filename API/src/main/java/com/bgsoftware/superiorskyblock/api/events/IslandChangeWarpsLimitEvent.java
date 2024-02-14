package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeWarpsLimitEvent is called when the warps limit of an plot is changed.
 */
public class PlotChangeWarpsLimitEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private int warpsLimit;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the warps limit of an plot.
     *                       If set to null, it means the limit was changed via the console.
     * @param plot         The plot that the warps limit was changed for.
     * @param warpsLimit     The new warps limit of an plot.
     */
    public PlotChangeWarpsLimitEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, int warpsLimit) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.warpsLimit = warpsLimit;
    }

    /**
     * Get the player that changed the warps limit.
     * If null, it means the limit was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new warps limit of the plot.
     */
    public int getWarpsLimit() {
        return warpsLimit;
    }

    /**
     * Set the new warps limit of the plot.
     *
     * @param warpsLimit The new warps limit to set.
     */
    public void setWarpsLimit(int warpsLimit) {
        Preconditions.checkArgument(warpsLimit >= 0, "Cannot set the warps limit to a negative limit.");
        this.warpsLimit = warpsLimit;
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
