package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangeBorderSizeEvent is called when the border-size of the plot is changed.
 */
public class PlotChangeBorderSizeEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private int borderSize;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the border size of the plot.
     *                       If set to null, it means the limit was changed by console.
     * @param plot         The plot that the border size was changed for.
     * @param borderSize     The new border size of the plot
     */
    public PlotChangeBorderSizeEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, int borderSize) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.borderSize = borderSize;
    }

    /**
     * Get the player that changed the border-size of the plot.
     * If null, it means the size was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new border size of the plot.
     */
    public int getBorderSize() {
        return borderSize;
    }

    /**
     * Set the new border size for the plot.
     *
     * @param borderSize The new border size to set.
     */
    public void setBorderSize(int borderSize) {
        Preconditions.checkArgument(borderSize >= 1, "Cannot set the border size to values lower than 1.");
        this.borderSize = borderSize;
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
