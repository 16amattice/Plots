package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;

import java.math.BigDecimal;

/**
 * PlotChangeBankLimitEvent is called when the bank-limit of the plot is changed.
 */
public class PlotChangeBankLimitEvent extends PlotEvent implements Cancellable {

    @Nullable
    private final SuperiorPlayer superiorPlayer;

    private BigDecimal bankLimit;
    private boolean cancelled = false;

    /**
     * The constructor of the event.
     *
     * @param superiorPlayer The player that changed the bank limit of the plot.
     *                       If set to null, it means the limit was changed by console.
     * @param plot         The plot that the bank limit was changed for.
     * @param bankLimit      The new bank limit of the plot
     */
    public PlotChangeBankLimitEvent(@Nullable SuperiorPlayer superiorPlayer, Plot plot, BigDecimal bankLimit) {
        super(plot);
        this.superiorPlayer = superiorPlayer;
        this.bankLimit = bankLimit;
    }

    /**
     * Get the player that changed the bank limit of the plot.
     * If null, it means the limit was changed by console.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return superiorPlayer;
    }

    /**
     * Get the new bank limit of the plot.
     */
    public BigDecimal getBankLimit() {
        return bankLimit;
    }

    /**
     * Set the new bank limit for the plot.
     *
     * @param bankLimit The new bank limit to set.
     */
    public void setBankLimit(BigDecimal bankLimit) {
        Preconditions.checkNotNull(bankLimit, "Cannot set the bank limit to null.");
        Preconditions.checkArgument(bankLimit.compareTo(BigDecimal.ZERO) >= 0, "Cannot set the bank limit to a negative limit.");
        this.bankLimit = bankLimit;
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
