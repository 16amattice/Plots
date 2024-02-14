package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.event.Cancellable;

/**
 * PlotTransferEvent is called when the leadership of an plot is transferred.
 */
public class PlotTransferEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer oldOwner;
    private final SuperiorPlayer newOwner;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param plot   The plot that the leadership of it is transferred.
     * @param oldOwner The old owner of the plot.
     * @param newOwner The new owner of the plot.
     */
    public PlotTransferEvent(Plot plot, SuperiorPlayer oldOwner, SuperiorPlayer newOwner) {
        super(plot);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    /**
     * Get the old owner of the plot.
     */
    public SuperiorPlayer getOldOwner() {
        return oldOwner;
    }

    /**
     * Get the new owner of the plot.
     */
    public SuperiorPlayer getNewOwner() {
        return newOwner;
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
