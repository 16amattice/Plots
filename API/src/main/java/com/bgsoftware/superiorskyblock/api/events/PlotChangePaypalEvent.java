package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

/**
 * PlotChangePaypalEvent is called when the paypal of the plot is changed.
 */
public class PlotChangePaypalEvent extends PlotEvent implements Cancellable {

    private final SuperiorPlayer superiorPlayer;

    private String paypal;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the paypal of the plot.
     * @param plot         The plot that the paypal was changed for.
     * @param paypal         The new paypal of the plot
     */
    public PlotChangePaypalEvent(SuperiorPlayer superiorPlayer, Plot plot, String paypal) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.paypal = paypal;
    }

    /**
     * Get the player that changed the paypal of the plot.
     */
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new paypal of the plot.
     */
    public String getPaypal() {
        return paypal;
    }

    /**
     * Set the new paypal for the plot.
     *
     * @param paypal The new paypal to set.
     */
    public void setPaypal(String paypal) {
        Preconditions.checkNotNull(paypal, "Cannot set the discord of the plot to null.");
        this.paypal = paypal;
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
