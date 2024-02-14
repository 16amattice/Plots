package com.bgsoftware.superiorskyblock.api.events;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.api.plot.Plot;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;

import java.math.BigDecimal;

/**
 * PlotWorthCalculatedEvent is called when the worth of an plot is calculated.
 */
public class PlotWorthCalculatedEvent extends PlotEvent {

    private final BigDecimal level;
    private final BigDecimal worth;
    @Nullable
    private final SuperiorPlayer player;

    /**
     * The constructor of the event.
     *
     * @param plot The plot that it's worth was calculated.
     * @param player The player who requested the operation (may be null).
     * @param level  The new level of the plot.
     * @param worth  The new worth value of the plot.
     */
    public PlotWorthCalculatedEvent(Plot plot, @Nullable SuperiorPlayer player, BigDecimal level, BigDecimal worth) {
        super(plot);
        this.player = player;
        this.level = level;
        this.worth = worth;
    }

    /**
     * Get the player who requested the operation.
     * Can be null if the console called the operation.
     */
    @Nullable
    public SuperiorPlayer getPlayer() {
        return player;
    }

    /**
     * Get the new level of the plot.
     */
    public BigDecimal getLevel() {
        return level;
    }

    /**
     * Get the new worth value of the plot.
     */
    public BigDecimal getWorth() {
        return worth;
    }
}
